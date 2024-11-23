package ru.practicum.workshop.registrationservice.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.workshop.registrationservice.dto.AuthRegistrationDto;
import ru.practicum.workshop.registrationservice.dto.NewRegistrationDto;
import ru.practicum.workshop.registrationservice.dto.PublicRegistrationDto;
import ru.practicum.workshop.registrationservice.dto.UpdateRegistrationDto;

import java.util.List;

public interface RegistrationService {

    AuthRegistrationDto createRegistration(NewRegistrationDto newRegistrationDto);

    PublicRegistrationDto updateRegistrationData(UpdateRegistrationDto updateRegistrationDto);

    void deleteRegistration(AuthRegistrationDto authRegistrationDto);

    PublicRegistrationDto getRegistration(Long registrationId);

    List<PublicRegistrationDto> getRegistrations(Long eventId, Pageable pageable);

}
