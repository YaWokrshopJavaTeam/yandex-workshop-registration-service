package ru.practicum.workshop.registrationservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.workshop.registrationservice.dto.*;
import ru.practicum.workshop.registrationservice.exception.AuthenticationException;
import ru.practicum.workshop.registrationservice.mapping.RegistrationMapper;
import ru.practicum.workshop.registrationservice.model.Registration;
import ru.practicum.workshop.registrationservice.model.RegistrationStatus;
import ru.practicum.workshop.registrationservice.repository.RegistrationRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationRepository registrationRepository;

    private final RegistrationMapper registrationMapper;

    @Override
    @Transactional
    public AuthRegistrationDto createRegistration(NewRegistrationDto newRegistrationDto) {
        Registration newRegistration = registrationMapper.toRegistration(newRegistrationDto, getRandomPassword());

        newRegistration.setRegistrationStatus(RegistrationStatus.PENDING.toString());
        newRegistration.setCreatedAt(LocalDateTime.now());

        registrationRepository.save(newRegistration);

        log.info("Registration added: {}", newRegistration);

        return registrationMapper.toAuthRegistrationDto(newRegistration);
    }

    @Override
    @Transactional
    public PublicRegistrationDto updateRegistrationData(UpdateRegistrationDto updateRegistrationDto) {
        Registration registration = getRegistrationInternal(updateRegistrationDto.getId());

        if (!registration.getPassword().equals(updateRegistrationDto.getPassword())) {
            throw new AuthenticationException(
                    String.format("Incorrect password for registration with id=%d", updateRegistrationDto.getId()));
        }

        registrationMapper.updateRegistrationData(registration, updateRegistrationDto);
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
            Optional<Registration> waitingRegistration = registrationRepository
                    .findEarliestRegistrationByStatusNative(RegistrationStatus.WAITING.toString());
            if (waitingRegistration.isPresent()) {
                Registration waiting = waitingRegistration.get();
                waiting.setRegistrationStatus(RegistrationStatus.PENDING.toString());
                registrationRepository.save(waiting);
                log.info("Registration with id={} update status from WAITING to PENDING", waiting.getId());
            }
        }

        registrationRepository.deleteById(authRegistrationDto.getId());

        log.info("Registration with id={} was deleted.", authRegistrationDto.getId());
    }

    @Override
    public PublicRegistrationDto getRegistration(Long registrationId) {
        Registration registration = getRegistrationInternal(registrationId);

        log.info("Sent registration with id={}.", registrationId);

        return registrationMapper.toPublicRegistrationDto(registration);
    }

    @Override
    public List<PublicRegistrationDto> getRegistrations(Long eventId, Pageable pageable) {
        List<Registration> registrations = registrationRepository.findAllByEventId(eventId, pageable);

        log.info("Sent registrations for event id={}, page={}, size={}.",
                eventId, pageable.getPageNumber(), pageable.getPageSize());

        return registrationMapper.toPublicRegistrationDto(registrations);
    }

    @Transactional
    public PublicRegistrationStatusDto updateRegistrationStatus(UpdateStatusDto updateStatusDto) {
        RegistrationStatus status = RegistrationStatus.parseStatus(updateStatusDto.getStatus());

        Registration registrationToUpdateStatus = getRegistrationInternal(updateStatusDto.getId());
        registrationToUpdateStatus.setRegistrationStatus(status.toString());

        registrationRepository.save(registrationToUpdateStatus);

        log.info("update registration status id={}", updateStatusDto.getId());

        if (updateStatusDto.getStatus().equals(RegistrationStatus.REJECTED.toString())) {
            if (updateStatusDto.getReason() == null) {
                throw new ValidationException("Reason not be null with status REJECTED");
            }
            return registrationMapper.toStatusRegistrationDtoWithReason(registrationToUpdateStatus,
                    updateStatusDto.getReason());
        }
        return registrationMapper.toStatusRegistrationDtoWithoutReason(registrationToUpdateStatus);
    }

    @Transactional(readOnly = true)
    public List<PublicRegistrationStatusDto> getRegistrationsWithStatusesAndEventId(Long eventId, List<String> statuses) {
        List<RegistrationStatus> statusesFromRequest = statuses.stream()
                .map(RegistrationStatus::parseStatus)
                .toList();

        List<Registration> registrations = registrationRepository.findAllByEventIdOrderByCreatedAt(eventId);

        log.info("Sent registrations with eventId={} and statuses {}.", eventId, statusesFromRequest);

        if (statusesFromRequest.isEmpty()) return registrationMapper.toListStatusRegistrationDto(registrations);

        List<PublicRegistrationStatusDto> r1 = registrations.stream()
                .filter(r -> statusesFromRequest.contains(RegistrationStatus.parseStatus(r.getRegistrationStatus())))
                .map(registrationMapper::toStatusRegistrationDtoWithoutReason)
                .toList();
        return r1;
    }

    @Transactional(readOnly = true)
    public PublicRegistrationCountDto countRegistrationsByStatus(Long eventId, String status) {
        RegistrationStatus statusFromRequest = RegistrationStatus.parseStatus(status);

        log.info("Sent count registrations with eventId={} and status {}.", eventId, statusFromRequest);

        return new PublicRegistrationCountDto(statusFromRequest.toString(),
                registrationRepository.countByEventIdAndRegistrationStatus(eventId, status));
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
}
