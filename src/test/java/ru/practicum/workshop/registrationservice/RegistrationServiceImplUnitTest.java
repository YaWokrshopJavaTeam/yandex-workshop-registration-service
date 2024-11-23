package ru.practicum.workshop.registrationservice;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.workshop.registrationservice.dto.AuthRegistrationDto;
import ru.practicum.workshop.registrationservice.dto.NewRegistrationDto;
import ru.practicum.workshop.registrationservice.dto.PublicRegistrationDto;
import ru.practicum.workshop.registrationservice.dto.UpdateRegistrationDto;
import ru.practicum.workshop.registrationservice.exception.AuthenticationException;
import ru.practicum.workshop.registrationservice.mapping.RegistrationMapper;
import ru.practicum.workshop.registrationservice.model.Registration;
import ru.practicum.workshop.registrationservice.repository.RegistrationRepository;
import ru.practicum.workshop.registrationservice.service.RegistrationServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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

        AuthRegistrationDto actualAuthRegistrationDto = registrationService.createRegistration(newRegistrationDto);

        assertThat(actualAuthRegistrationDto.getId(), equalTo(expectedAuthRegistrationDto.getId()));
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

}
