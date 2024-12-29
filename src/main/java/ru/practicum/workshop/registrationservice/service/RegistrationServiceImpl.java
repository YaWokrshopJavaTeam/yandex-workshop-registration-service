package ru.practicum.workshop.registrationservice.service;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.workshop.registrationservice.client.EventClient;
import ru.practicum.workshop.registrationservice.client.UserClient;
import ru.practicum.workshop.registrationservice.client.dto.EventResponse;
import ru.practicum.workshop.registrationservice.client.dto.PublicOrgTeamMemberDto;
import ru.practicum.workshop.registrationservice.client.dto.UpdateUserFromRegistrationDto;
import ru.practicum.workshop.registrationservice.dto.*;
import ru.practicum.workshop.registrationservice.exception.AuthenticationException;
import ru.practicum.workshop.registrationservice.exception.ConflictException;
import ru.practicum.workshop.registrationservice.mapping.RegistrationMapper;
import ru.practicum.workshop.registrationservice.model.Registration;
import ru.practicum.workshop.registrationservice.model.RegistrationStatus;
import ru.practicum.workshop.registrationservice.repository.RegistrationRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {
    private final RegistrationRepository registrationRepository;
    private final RegistrationMapper registrationMapper;
    private final UserClient userClient;
    private final EventClient eventClient;

    @Override
    @Transactional
    public AuthRegistrationDto createRegistration(NewRegistrationDto newRegistrationDto) {
        Registration newRegistration = registrationMapper.toRegistration(newRegistrationDto, getRandomPassword(),
                RegistrationStatus.PENDING.toString(), LocalDateTime.now());

        try {
            EventResponse eventResponse = eventClient.getEvent(newRegistrationDto.getEventId());
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException(
                    String.format("Event (id=%d) doesn't exist.", newRegistrationDto.getEventId()));
        }

        NewUserDto newUserDto = new NewUserDto("autoUser", newRegistration.getEmail(), "autoPassword",
                "Auto registration from registration service.");

        newRegistration.setUserId(userClient.autoCreateUser(newUserDto));

        registrationRepository.save(newRegistration);

        log.info("Registration added: {}", newRegistration);

        return registrationMapper.toAuthRegistrationDto(newRegistration);
    }

    @Override
    @Transactional
    public PublicRegistrationDto updateRegistrationData(UpdateRegistrationDto updateRegistrationDto) {
        Registration registration = getRegistrationInternal(updateRegistrationDto.getId());

        if (!registration.getPassword().equals(updateRegistrationDto.getPassword())) {
            throw new AuthenticationException(String.format("Incorrect password for registration with id=%d", updateRegistrationDto.getId()));
        }

        registrationMapper.updateRegistrationData(registration, updateRegistrationDto);

        UpdateUserFromRegistrationDto updateUserFromRegistrationDto = new UpdateUserFromRegistrationDto(registration.getEmail());
        userClient.autoUpdateUser(updateUserFromRegistrationDto, registration.getUserId());

        registrationRepository.save(registration);

        log.info("Registration data updated: {}", registration);

        return registrationMapper.toPublicRegistrationDto(registration);
    }

    @Override
    @Transactional
    public void deleteRegistration(AuthRegistrationDto authRegistrationDto) {
        Registration registration = getRegistrationInternal(authRegistrationDto.getId());

        if (!registration.getPassword().equals(authRegistrationDto.getPassword())) {
            throw new AuthenticationException(
                    String.format("Incorrect password for registration with id=%d", authRegistrationDto.getId()));
        }

        if (registration.getRegistrationStatus().equals(RegistrationStatus.APPROVED.toString())) {

            EventResponse eventResponse = eventClient.getEvent(registration.getEventId(), registration.getUserId());
            if (LocalDateTime.now().isAfter(eventResponse.getStartDateTime()) &&
                    LocalDateTime.now().isBefore(eventResponse.getEndDateTime())) {
                throw new ValidationException("You can't delete registration. Event id=" + registration.getEventId() + " is already started.");
            }

            Optional<Registration> waitingRegistration = registrationRepository
                    .findFirstByRegistrationStatusOrderByCreatedAtAsc(RegistrationStatus.WAITING.toString());
            if (waitingRegistration.isPresent()) {
                Registration waiting = waitingRegistration.get();
                waiting.setRegistrationStatus(RegistrationStatus.PENDING.toString());
                registrationRepository.save(waiting);
                log.info("Registration with id={} update status from WAITING to PENDING", waiting.getId());
            }
        }

        long numberOfUserRegistrations = registrationRepository.countByUserId(registration.getUserId());
        if (numberOfUserRegistrations == 1) {
            userClient.autoDeleteUser(registration.getUserId());
        }

        registrationRepository.deleteById(authRegistrationDto.getId());

        log.info("Registration with id={} was deleted.", authRegistrationDto.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public PublicRegistrationDto getRegistration(Long registrationId) {
        Registration registration = getRegistrationInternal(registrationId);

        log.info("Sent registration with id={}.", registrationId);

        return registrationMapper.toPublicRegistrationDto(registration);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublicRegistrationDto> getRegistrations(Long eventId, Pageable pageable) {
        List<Registration> registrations = registrationRepository.findAllByEventId(eventId, pageable);

        log.info("Sent registrations for event id={}, page={}, size={}.",
                eventId, pageable.getPageNumber(), pageable.getPageSize());

        return registrationMapper.toPublicRegistrationDto(registrations);
    }

    @Override
    @Transactional
    public PublicRegistrationStatusDto updateRegistrationStatus(Long requesterId, UpdateStatusDto updateStatusDto) {
        RegistrationStatus status = RegistrationStatus.parseStatus(updateStatusDto.getStatus());

        Registration registrationToUpdateStatus = getRegistrationInternal(updateStatusDto.getId());

        if (!RegistrationStatus.isTransitionValid(
                RegistrationStatus.parseStatus(registrationToUpdateStatus.getRegistrationStatus()),
                status)) {
            throw new ConflictException(String.format("Registration (id=%d) with status=%s can't be transitioned to %s.",
                                                      registrationToUpdateStatus.getId(),
                                                      registrationToUpdateStatus.getRegistrationStatus(),
                                                      updateStatusDto.getStatus()));
        }

        EventResponse eventResponse;
        try {
            eventResponse = eventClient.getEvent(registrationToUpdateStatus.getEventId());
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException(String.format("Event (id=%d) doesn't exist.",
                                                             registrationToUpdateStatus.getEventId()));
        }

        if (!eventResponse.getOwnerId().equals(requesterId)) {
            List<PublicOrgTeamMemberDto> eventTeamMembers = eventClient.getEventTeamMembers(eventResponse.getId());
            eventTeamMembers.stream()
                .filter(member -> member.getUserId().equals(requesterId) && member.getRole().equals(PublicOrgTeamMemberDto.Role.MANAGER))
                .findFirst().orElseThrow(() -> new AuthenticationException(
                        String.format("Requester (id=%d) can't modify status of event (id=%d).",
                                      requesterId,
                                      eventResponse.getId())));
        }

        if (updateStatusDto.getStatus().equals(RegistrationStatus.REJECTED.toString())
                && updateStatusDto.getReason() == null) {
            throw new ValidationException("Reason can't be null with status REJECTED");
        }

        registrationToUpdateStatus.setRegistrationStatus(status.toString());
        registrationRepository.save(registrationToUpdateStatus);

        log.info("update registration status id={}", updateStatusDto.getId());

        PublicRegistrationStatusDto publicRegistrationStatusDto =
                registrationMapper.toStatusRegistrationDtoWithReason(registrationToUpdateStatus, updateStatusDto.getReason());
        return publicRegistrationStatusDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublicRegistrationStatusDto> getRegistrationsWithStatusesAndEventId(Long eventId, List<String> statuses) {
        List<RegistrationStatus> statusesFromRequest = statuses.stream()
                .map(RegistrationStatus::parseStatus)
                .toList();

        if (statusesFromRequest.isEmpty()) {
            return List.of();
        }

        List<Registration> registrations = registrationRepository
                .findAllByEventIdAndRegistrationStatusInOrderByCreatedAt(eventId,statuses);

        log.info("Sent registrations with eventId={} and statuses {}.", eventId, statusesFromRequest);

        return registrations.stream()
                .map(registrationMapper::toStatusRegistrationDtoWithoutReason)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> countRegistrationsByStatus(Long eventId) {
        List<Object[]> response = registrationRepository.getListByEventIdAndGroupByRegistrationStatus(eventId);

        if (response.isEmpty()) return Map.of();

        log.info("Sent count registrations with eventId={}.", eventId);

        return response.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> ((Number) row[1]).longValue()
                ));
    }

    private String getRandomPassword() {
        // Use seconds number from 1970 as random seed.
        Random random = new Random(LocalDateTime.now().toInstant(ZoneOffset.UTC).getEpochSecond());

        // Return 4-digit password
        return String.format("%04d", random.nextInt(10000));
    }

    @Transactional(readOnly = true)
    private Registration getRegistrationInternal(Long registrationId) {
        return registrationRepository.findById(registrationId).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Registration with id=%d not found.", registrationId)));
    }

    @Override
    @Transactional(readOnly = true)
    public String getStatusOfRegistration(Long eventId, Long userId) {
        Registration registration = registrationRepository.findByEventIdAndUserId(eventId, userId).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Registration from user id=%d to event id=%d not found.", userId, eventId)));

        return registration.getRegistrationStatus();
    }
}
