package ru.practicum.workshop.registrationservice.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.workshop.registrationservice.dto.*;

import java.util.List;
import java.util.Map;

public interface RegistrationService {

    AuthRegistrationDto createRegistration(NewRegistrationDto newRegistrationDto);

    PublicRegistrationDto updateRegistrationData(UpdateRegistrationDto updateRegistrationDto);

    void deleteRegistration(AuthRegistrationDto authRegistrationDto);

    PublicRegistrationDto getRegistration(Long registrationId);

    List<PublicRegistrationDto> getRegistrations(Long eventId, Pageable pageable);

    PublicRegistrationStatusDto updateRegistrationStatus(UpdateStatusDto updateStatusDto);

    List<PublicRegistrationStatusDto> getRegistrationsWithStatusesAndEventId(Long eventId, List<String> statuses);

    Map<String, Long> countRegistrationsByStatus(Long eventId);
}
