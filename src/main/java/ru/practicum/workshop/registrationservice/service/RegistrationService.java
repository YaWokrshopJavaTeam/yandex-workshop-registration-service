package ru.practicum.workshop.registrationservice.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.workshop.registrationservice.dto.*;

import java.util.List;

public interface RegistrationService {

    AuthRegistrationDto createRegistration(NewRegistrationDto newRegistrationDto);

    PublicRegistrationDto updateRegistrationData(UpdateRegistrationDto updateRegistrationDto);

    void deleteRegistration(AuthRegistrationDto authRegistrationDto);

    PublicRegistrationDto getRegistration(Long registrationId);

    List<PublicRegistrationDto> getRegistrations(Long eventId, Pageable pageable);

    PublicRegistrationStatusDto updateRegistrationStatus(UpdateStatusDto updateStatusDto);

    List<PublicRegistrationStatusDto> getRegistrationsWithStatusesAndEventId(Long eventId, List<String> statuses);

    PublicRegistrationCountDto countRegistrationsByStatus(Long eventId, String status);
}
