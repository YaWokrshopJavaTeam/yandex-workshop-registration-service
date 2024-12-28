package ru.practicum.workshop.registrationservice;

import feign.FeignException;
import feign.Request;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.exceptions.misusing.PotentialStubbingProblem;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.workshop.registrationservice.client.EventClient;
import ru.practicum.workshop.registrationservice.client.dto.EventResponse;
import ru.practicum.workshop.registrationservice.client.dto.PublicOrgTeamMemberDto;
import ru.practicum.workshop.registrationservice.dto.*;
import ru.practicum.workshop.registrationservice.exception.AuthenticationException;
import ru.practicum.workshop.registrationservice.mapping.RegistrationMapper;
import ru.practicum.workshop.registrationservice.model.Registration;
import ru.practicum.workshop.registrationservice.model.RegistrationStatus;
import ru.practicum.workshop.registrationservice.repository.RegistrationRepository;
import ru.practicum.workshop.registrationservice.service.RegistrationServiceImpl;
import ru.practicum.workshop.registrationservice.client.UserClient;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceImplUnitTest {

    @Spy
    private RegistrationMapper registrationMapper = Mappers.getMapper(RegistrationMapper.class);

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private EventClient eventClient;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    // Method "createRegistration" tests.
    @Test
    public void createRegistration_whenInputValid_thenSave() {
        NewRegistrationDto newRegistrationDto = NewRegistrationDto.builder()
                .name("Yury")
                .email("yury@yandex.ru")
                .phone("+79991234567")
                .eventId(1L).build();

        AuthRegistrationDto expectedAuthRegistrationDto = AuthRegistrationDto.builder()
                .id(1L).build();

        when(registrationRepository.save(any(Registration.class)))
                .thenAnswer(invocation -> {
                    Registration argument = invocation.getArgument(0);
                    argument.setId(1L);
                    return argument;
                });

        Long userId = 1L;
        when(userClient.autoCreateUser(any(NewUserDto.class)))
                .thenReturn(userId);

        EventResponse eventResponse = new EventResponse(
                1L,
                "Event",
                "Description",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                "Location",
                2L,
                null);
        when(eventClient.getEvent(any(Long.class))).thenReturn(eventResponse);

        AuthRegistrationDto actualAuthRegistrationDto = registrationService.createRegistration(newRegistrationDto);

        assertThat(actualAuthRegistrationDto.getId(), equalTo(expectedAuthRegistrationDto.getId()));
    }

    @Test
    public void createRegistration_whenEventNotExists_thenThrowException() {
        NewRegistrationDto newRegistrationDto = NewRegistrationDto.builder()
                .name("Yury")
                .email("yury@yandex.ru")
                .phone("+79991234567")
                .eventId(1L).build();

        EventResponse eventResponse = new EventResponse(
                1L,
                "Event",
                "Description",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                "Location",
                2L,
                null);
        when(eventClient.getEvent(any(Long.class)))
                .thenThrow(new FeignException.NotFound(
                        null,
                        Request.create(Request.HttpMethod.GET, "url", new HashMap<>(), null, null, null),
                        null,
                        new HashMap<>()));

        assertThrows(EntityNotFoundException.class, () -> registrationService.createRegistration(newRegistrationDto));
    }

    // Method "updateRegistrationData" tests.
    @Test
    public void updateRegistrationData_whenInputValid_thenUpdate() {
        UpdateRegistrationDto updateRegistrationDto = UpdateRegistrationDto.builder()
                .id(1L)
                .password("1234")
                .name("Yuri")
                .email("yuri@yandex.ru")
                .phone("+79991234560").build();

        Registration registration = Registration.builder()
                .id(1L)
                .password("1234")
                .name("Yury")
                .email("yury@yandex.ru")
                .phone("+79991234567")
                .eventId(1L).build();

        PublicRegistrationDto expectedPublicRegistrationDto = PublicRegistrationDto.builder()
                .name("Yuri")
                .email("yuri@yandex.ru")
                .phone("+79991234560")
                .eventId(1L).build();

        when(registrationRepository.findById(any(Long.class))).thenReturn(Optional.of(registration));

        PublicRegistrationDto actualPublicRegistrationDto = registrationService.updateRegistrationData(updateRegistrationDto);

        assertThat(actualPublicRegistrationDto, equalTo(expectedPublicRegistrationDto));
    }

    @Test
    public void updateRegistrationData_withNameOnly_thenUpdate() {
        UpdateRegistrationDto updateRegistrationDto = UpdateRegistrationDto.builder()
                .id(1L)
                .password("1234")
                .name("Yuri").build();

        Registration registration = Registration.builder()
                .id(1L)
                .password("1234")
                .name("Yury")
                .email("yury@yandex.ru")
                .phone("+79991234567")
                .eventId(1L).build();

        PublicRegistrationDto expectedPublicRegistrationDto = PublicRegistrationDto.builder()
                .name("Yuri")
                .email("yury@yandex.ru")
                .phone("+79991234567")
                .eventId(1L).build();

        when(registrationRepository.findById(any(Long.class))).thenReturn(Optional.of(registration));

        PublicRegistrationDto actualPublicRegistrationDto = registrationService.updateRegistrationData(updateRegistrationDto);

        assertThat(actualPublicRegistrationDto, equalTo(expectedPublicRegistrationDto));
    }

    @Test
    public void updateRegistrationData_withEmailOnly_thenUpdate() {
        UpdateRegistrationDto updateRegistrationDto = UpdateRegistrationDto.builder()
                .id(1L)
                .password("1234")
                .email("yuri@yandex.ru").build();

        Registration registration = Registration.builder()
                .id(1L)
                .password("1234")
                .name("Yury")
                .email("yury@yandex.ru")
                .phone("+79991234567")
                .eventId(1L).build();

        PublicRegistrationDto expectedPublicRegistrationDto = PublicRegistrationDto.builder()
                .name("Yury")
                .email("yuri@yandex.ru")
                .phone("+79991234567")
                .eventId(1L).build();

        when(registrationRepository.findById(any(Long.class))).thenReturn(Optional.of(registration));

        PublicRegistrationDto actualPublicRegistrationDto = registrationService.updateRegistrationData(updateRegistrationDto);

        assertThat(actualPublicRegistrationDto, equalTo(expectedPublicRegistrationDto));
    }

    @Test
    public void updateRegistrationData_withPhoneOnly_thenUpdate() {
        UpdateRegistrationDto updateRegistrationDto = UpdateRegistrationDto.builder()
                .id(1L)
                .password("1234")
                .phone("+79991234560").build();

        Registration registration = Registration.builder()
                .id(1L)
                .password("1234")
                .name("Yury")
                .email("yury@yandex.ru")
                .phone("+79991234567")
                .eventId(1L).build();

        PublicRegistrationDto expectedPublicRegistrationDto = PublicRegistrationDto.builder()
                .name("Yury")
                .email("yury@yandex.ru")
                .phone("+79991234560")
                .eventId(1L).build();

        when(registrationRepository.findById(any(Long.class))).thenReturn(Optional.of(registration));

        PublicRegistrationDto actualPublicRegistrationDto = registrationService.updateRegistrationData(updateRegistrationDto);

        assertThat(actualPublicRegistrationDto, equalTo(expectedPublicRegistrationDto));
    }

    @Test
    public void updateRegistrationData_whenRegistrationNotExists_thenThrowException() {
        UpdateRegistrationDto updateRegistrationDto = UpdateRegistrationDto.builder()
                .id(1L)
                .password("1234")
                .name("Yuri")
                .email("yuri@yandex.ru")
                .phone("+79991234560").build();

        when(registrationRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> registrationService.updateRegistrationData(updateRegistrationDto));
    }

    @Test
    public void updateRegistrationData_whenPasswordInvalid_thenThrowException() {
        UpdateRegistrationDto updateRegistrationDto = UpdateRegistrationDto.builder()
                .id(1L)
                .password("1111")
                .name("Yuri")
                .email("yuri@yandex.ru")
                .phone("+79991234560").build();

        Registration registration = Registration.builder()
                .id(1L)
                .password("1234")
                .name("Yury")
                .email("yury@yandex.ru")
                .phone("+79991234567")
                .eventId(1L).build();

        when(registrationRepository.findById(any(Long.class))).thenReturn(Optional.of(registration));

        assertThrows(AuthenticationException.class, () -> registrationService.updateRegistrationData(updateRegistrationDto));
    }

    // Method "deleteRegistration" tests.
    @Test
    public void deleteRegistration_whenInputValid_thenDelete() {
        AuthRegistrationDto authRegistrationDto = AuthRegistrationDto.builder()
                .id(1L)
                .password("1234").build();

        Registration registration = Registration.builder()
                .id(1L)
                .password("1234")
                .name("Yury")
                .email("yury@yandex.ru")
                .phone("+79991234567")
                .registrationStatus(RegistrationStatus.PENDING.toString())
                .eventId(1L).build();

        when(registrationRepository.findById(any(Long.class))).thenReturn(Optional.of(registration));

        registrationService.deleteRegistration(authRegistrationDto);

        verify(registrationRepository).deleteById(1L);
    }

    @Test
    public void deleteRegistration_whenRegistrationNotExists_thenThrowException() {
        AuthRegistrationDto authRegistrationDto = AuthRegistrationDto.builder()
                .id(1L)
                .password("1234").build();

        when(registrationRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> registrationService.deleteRegistration(authRegistrationDto));
    }

    @Test
    public void deleteRegistration_whenPasswordInvalid_thenThrowException() {
        AuthRegistrationDto authRegistrationDto = AuthRegistrationDto.builder()
                .id(1L)
                .password("1111").build();

        Registration registration = Registration.builder()
                .id(1L)
                .password("1234")
                .name("Yury")
                .email("yury@yandex.ru")
                .phone("+79991234567")
                .eventId(1L).build();

        when(registrationRepository.findById(any(Long.class))).thenReturn(Optional.of(registration));

        assertThrows(AuthenticationException.class, () -> registrationService.deleteRegistration(authRegistrationDto));
    }

    // Method "getRegistration" tests.
    @Test
    public void getRegistration_whenInputValid_thenReturn() {
        Long registrationId = 1L;

        Registration registration = Registration.builder()
                .id(1L)
                .password("1234")
                .name("Yury")
                .email("yury@yandex.ru")
                .phone("+79991234567")
                .eventId(1L).build();

        PublicRegistrationDto expectedPublicRegistrationDto = PublicRegistrationDto.builder()
                .name("Yury")
                .email("yury@yandex.ru")
                .phone("+79991234567")
                .eventId(1L).build();

        when(registrationRepository.findById(any(Long.class))).thenReturn(Optional.of(registration));

        PublicRegistrationDto actualPublicRegistrationDto = registrationService.getRegistration(registrationId);

        assertThat(actualPublicRegistrationDto, equalTo(expectedPublicRegistrationDto));
    }

    @Test
    public void getRegistration_whenRegistrationNotExists_thenThrowException() {
        Long registrationId = 1L;

        when(registrationRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> registrationService.getRegistration(registrationId));
    }

    // Method "getRegistrations" tests.
    @Test
    public void getRegistrations_whenInputValid_thenReturn() {
        Long eventId = 1L;
        Pageable pageable = PageRequest.of(0, 2);

        List<Registration> registrationList = List.of(
                Registration.builder()
                        .id(1L)
                        .password("1234")
                        .name("Yury")
                        .email("yury@yandex.ru")
                        .phone("+79991234567")
                        .eventId(1L).build(),
                Registration.builder()
                        .id(2L)
                        .password("5678")
                        .name("Igor")
                        .email("igor@yandex.ru")
                        .phone("+79991234568")
                        .eventId(1L).build()
        );

        List<PublicRegistrationDto> expectedPublicRegistrationDto = List.of(
                PublicRegistrationDto.builder()
                        .name("Yury")
                        .email("yury@yandex.ru")
                        .phone("+79991234567")
                        .eventId(1L).build(),
                PublicRegistrationDto.builder()
                        .name("Igor")
                        .email("igor@yandex.ru")
                        .phone("+79991234568")
                        .eventId(1L).build()
        );

        when(registrationRepository.findAllByEventId(any(Long.class), any(Pageable.class)))
                .thenReturn(registrationList);

        List<PublicRegistrationDto> actualPublicRegistrationDto =
                registrationService.getRegistrations(eventId, pageable);

        assertThat(actualPublicRegistrationDto, equalTo(expectedPublicRegistrationDto));
    }

    @Test
    void updateStatus_shouldThrowExceptionForInvalidStatus() {
        Long requesterId = 777L;
        UpdateStatusDto invalidRequest = new UpdateStatusDto();
        invalidRequest.setStatus("INVALID_STATUS");

        Exception exception = assertThrows(ValidationException.class, () ->
                registrationService.updateRegistrationStatus(requesterId, invalidRequest)
        );

        assertEquals("Unknown status: INVALID_STATUS", exception.getMessage());
    }

    @Test
    void updateStatus_shouldThrowExceptionForNonExistingRegistration() {
        Long requesterId = 777L;
        Long registrationId = 999L;
        UpdateStatusDto request = new UpdateStatusDto();
        request.setStatus("APPROVED");

        Mockito.when(registrationRepository.findById(registrationId)).thenReturn(Optional.empty());


        assertThrows(PotentialStubbingProblem.class, () ->
                registrationService.updateRegistrationStatus(requesterId, request)
        );
    }

    @Test
    void updateStatus_whenRequesterIsEventOwner_shouldUpdateStatusSuccessfully() {
        Long requesterId = 777L;
        Long eventId = 100L;

        UpdateStatusDto request = new UpdateStatusDto();
        request.setStatus("APPROVED");
        request.setId(1L);

        Registration registration = new Registration();
        registration.setId(1L);
        registration.setRegistrationStatus("PENDING");
        registration.setEventId(eventId);

        Mockito.when(registrationRepository.findById(1L))
                .thenReturn(Optional.of(registration));

        EventResponse eventResponse = new EventResponse();
        eventResponse.setOwnerId(requesterId);
        Mockito.when(eventClient.getEvent(eventId)).thenReturn(eventResponse);

        registrationService.updateRegistrationStatus(requesterId, request);

        assertEquals("APPROVED", registration.getRegistrationStatus());
        Mockito.verify(registrationRepository).save(registration);
    }

    @Test
    void updateStatus_whenRequesterIsEventManager_shouldUpdateStatusSuccessfully() {
        Long requesterId = 777L;
        Long eventId = 100L;

        UpdateStatusDto request = new UpdateStatusDto();
        request.setStatus("APPROVED");
        request.setId(1L);

        Registration registration = new Registration();
        registration.setId(1L);
        registration.setRegistrationStatus("PENDING");
        registration.setEventId(eventId);

        Mockito.when(registrationRepository.findById(1L))
                .thenReturn(Optional.of(registration));

        EventResponse eventResponse = new EventResponse();
        eventResponse.setId(eventId);
        eventResponse.setOwnerId(requesterId + 1);
        Mockito.when(eventClient.getEvent(eventId)).thenReturn(eventResponse);

        PublicOrgTeamMemberDto publicOrgTeamMemberDto = new PublicOrgTeamMemberDto();
        publicOrgTeamMemberDto.setUserId(requesterId);
        publicOrgTeamMemberDto.setRole(PublicOrgTeamMemberDto.Role.MANAGER);
        Mockito.when(eventClient.getEventTeamMembers(eventId)).thenReturn(List.of(publicOrgTeamMemberDto));

        registrationService.updateRegistrationStatus(requesterId, request);

        assertEquals("APPROVED", registration.getRegistrationStatus());
        Mockito.verify(registrationRepository).save(registration);
    }

    @Test
    void countByStatus_shouldReturnCountSuccessfully() {
        Long eventId = 1L;

        List<Object[]> response = new ArrayList<>();
        response.add(new Object[]{"PENDING", 3L});
        Mockito.when(registrationRepository.getListByEventIdAndGroupByRegistrationStatus(eventId))
                .thenReturn(response);

        Map<String, Long> result = registrationService.countRegistrationsByStatus(eventId);

        assertNotNull(result);
        assertEquals(3L, result.values().toArray()[0]);
    }

    @Test
    void getRegistrationsWithStatusesAndEventId_shouldFilterRegistrationsByStatuses() {
        Long eventId = 1L;
        List<String> statuses = List.of("PENDING");

        List<Registration> mockRegistrations = List.of(
                new Registration(1L, 1L, "name", "email", "89993335544", 1L,
                        "PENDING", LocalDateTime.now(), "1234"));

        Mockito.when(registrationRepository.findAllByEventIdAndRegistrationStatusInOrderByCreatedAt(eventId, statuses))
                .thenReturn(mockRegistrations);

        Mockito.when(registrationMapper.toStatusRegistrationDtoWithoutReason(Mockito.any()))
                .thenReturn(new PublicRegistrationStatusDto("name", "email", "89993335544", 1L,
                        "PENDING", null, ""));

        List<PublicRegistrationStatusDto> result = registrationService.getRegistrationsWithStatusesAndEventId(eventId, statuses);

        assertEquals(1, result.size());
        Mockito.verify(registrationRepository).findAllByEventIdAndRegistrationStatusInOrderByCreatedAt(eventId, statuses);
        Mockito.verify(registrationMapper).toStatusRegistrationDtoWithoutReason(Mockito.any());
    }

    @Test
    void getRegistrationsWithStatusesAndEventId_shouldReturnEmptyListIfNoRegistrations() {
        Long eventId = 1L;
        List<String> statuses = List.of("PENDING");

        Mockito.when(registrationRepository.findAllByEventIdAndRegistrationStatusInOrderByCreatedAt(eventId, statuses))
                .thenReturn(Collections.emptyList());

        List<PublicRegistrationStatusDto> result = registrationService.getRegistrationsWithStatusesAndEventId(eventId, statuses);

        assertEquals(0, result.size());
        Mockito.verify(registrationRepository).findAllByEventIdAndRegistrationStatusInOrderByCreatedAt(eventId, statuses);
        Mockito.verifyNoInteractions(registrationMapper);
    }

    @Test
    void getRegistrationsWithStatusesAndEventId_shouldReturnAllRegistrationsIfStatusesNotProvided() {
        Long eventId = 1L;
        List<String> statuses = Collections.emptyList();

        List<Registration> mockRegistrations = List.of(
                new Registration(1L, 1L, "name", "email", "89993335544", 1L,
                        "PENDING", LocalDateTime.now(), "1234"),
                new Registration(2L, 2L, "name2", "email2", "89993335545", 1L,
                        "APPROVED", LocalDateTime.now(), "1235")
        );

        /*Mockito.when(registrationRepository.findAllByEventIdAndRegistrationStatusInOrderByCreatedAt(eventId, statuses))
                .thenReturn(mockRegistrations);*/

        /*Mockito.when(registrationMapper.toListStatusRegistrationDto(mockRegistrations))
                .thenReturn(List.of(
                        new PublicRegistrationStatusDto("name", "email", "89993335544", 1L,
                                "PENDING", null, ""),
                        new PublicRegistrationStatusDto("name2", "email2", "89993335545", 1L,
                                "APPROVED", null, "")
                ));*/

        List<PublicRegistrationStatusDto> result = registrationService.getRegistrationsWithStatusesAndEventId(eventId, statuses);

        assertEquals(0, result.size());
        //Mockito.verify(registrationRepository).findAllByEventIdAndRegistrationStatusInOrderByCreatedAt(eventId, statuses);
    }

    @Test
    void getRegistrationsWithStatusesAndEventId_shouldThrowExceptionForInvalidStatus() {
        Long eventId = 1L;
        List<String> statuses = List.of("INVALID_STATUS");

        Exception exception = assertThrows(ValidationException.class, () ->
                registrationService.getRegistrationsWithStatusesAndEventId(eventId, statuses)
        );

        assertEquals("Unknown status: INVALID_STATUS", exception.getMessage());
    }

}
