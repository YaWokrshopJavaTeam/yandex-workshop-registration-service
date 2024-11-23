package ru.practicum.workshop.registrationservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.workshop.registrationservice.dto.AuthRegistrationDto;
import ru.practicum.workshop.registrationservice.dto.NewRegistrationDto;
import ru.practicum.workshop.registrationservice.dto.PublicRegistrationDto;
import ru.practicum.workshop.registrationservice.dto.UpdateRegistrationDto;
import ru.practicum.workshop.registrationservice.exception.AuthenticationException;
import ru.practicum.workshop.registrationservice.mapping.RegistrationMapper;
import ru.practicum.workshop.registrationservice.model.Registration;
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
