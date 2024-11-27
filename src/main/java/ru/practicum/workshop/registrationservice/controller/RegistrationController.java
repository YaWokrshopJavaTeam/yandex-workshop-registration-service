package ru.practicum.workshop.registrationservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.workshop.registrationservice.dto.*;
import ru.practicum.workshop.registrationservice.service.RegistrationService;

import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthRegistrationDto createRegistration(@RequestBody @Valid NewRegistrationDto newRegistrationDto) {
        log.info("Request: create registration, newRegistrationDto={}", newRegistrationDto);
        return registrationService.createRegistration(newRegistrationDto);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public PublicRegistrationDto updateRegistrationData(@RequestBody @Valid UpdateRegistrationDto updateRegistrationDto) {
        log.info("Request: update registration data, updateRegistrationDto={}", updateRegistrationDto);
        return registrationService.updateRegistrationData(updateRegistrationDto);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRegistration(@RequestBody @Valid AuthRegistrationDto authRegistrationDto) {
        log.info("Request: delete registration, authRegistrationDto={}", authRegistrationDto);
        registrationService.deleteRegistration(authRegistrationDto);
    }

    @GetMapping("/{registrationId}")
    @ResponseStatus(HttpStatus.OK)
    public PublicRegistrationDto getRegistration(@PathVariable @Positive Long registrationId) {
        log.info("Request: get registration by id={}", registrationId);
        return registrationService.getRegistration(registrationId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PublicRegistrationDto> getRegistrations(@RequestParam("eventId") @Positive Long eventId,
                                                        Pageable pageable) {
        log.info("Request: get all registrations for event id={}, page={}, size={}",
                eventId, pageable.getPageNumber(), pageable.getPageSize());
        return registrationService.getRegistrations(eventId, pageable);
    }

    @PatchMapping("/status")
    @ResponseStatus(HttpStatus.OK)
    public PublicRegistrationStatusDto updateRegistrationStatus(@RequestBody @Valid UpdateStatusDto updateStatusDto) {
        log.info("Request: update registration status {}", updateStatusDto);
        return registrationService.updateRegistrationStatus(updateStatusDto);
    }

    @GetMapping("/status/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public List<PublicRegistrationStatusDto> getRegistrationsByStatusAndEventId(@PathVariable @Positive Long eventId,
                                                                                @RequestParam(value="status") List<String> statuses) {
        log.info("Request: get registrations with statuses {} and eventId {}", statuses, eventId);
        return registrationService.getRegistrationsWithStatusesAndEventId(eventId, statuses);
    }

    @GetMapping("/status/count")
    public Map<String, Long> countByStatus(@RequestParam("eventId") @Positive Long eventId) {
        log.info("Request: get count registrations with eventId {}", eventId);
        return registrationService.countRegistrationsByStatus(eventId);
    }
}
