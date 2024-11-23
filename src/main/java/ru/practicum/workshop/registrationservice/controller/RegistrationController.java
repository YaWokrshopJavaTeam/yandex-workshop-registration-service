package ru.practicum.workshop.registrationservice.controller;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.workshop.registrationservice.dto.AuthRegistrationDto;
import ru.practicum.workshop.registrationservice.dto.NewRegistrationDto;
import ru.practicum.workshop.registrationservice.dto.PublicRegistrationDto;
import ru.practicum.workshop.registrationservice.dto.UpdateRegistrationDto;
import ru.practicum.workshop.registrationservice.service.RegistrationService;

import java.util.List;

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
    public PublicRegistrationDto getRegistration(@PathVariable Long registrationId) {
        log.info("Request: get registration by id={}", registrationId);
        return registrationService.getRegistration(registrationId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PublicRegistrationDto> getRegistrations(@RequestParam("eventId") Long eventId, Pageable pageable) {
        log.info("Request: get all registrations for event id={}, page={}, size={}",
                eventId, pageable.getPageNumber(), pageable.getPageSize());
        return registrationService.getRegistrations(eventId, pageable);
    }

}
