package ru.practicum.workshop.registrationservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.workshop.registrationservice.controller.RegistrationController;
import ru.practicum.workshop.registrationservice.dto.*;
import ru.practicum.workshop.registrationservice.exception.AuthenticationException;
import ru.practicum.workshop.registrationservice.service.RegistrationService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RegistrationController.class)
public class RegistrationControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    public RegistrationService registrationService;

    // Method "createRegistration" tests.
    @Test
    public void createRegistration_whenInputValid_thenSave() throws Exception {
        NewRegistrationDto newRegistrationDto = NewRegistrationDto.builder()
                .name("Yury")
                .email("yury@yandex.ru")
                .phone("+79991234567")
                .eventId(1L).build();

        AuthRegistrationDto authRegistrationDto = AuthRegistrationDto.builder()
                .id(1L).password("1234").build();

        when(registrationService.createRegistration(any(NewRegistrationDto.class))).thenReturn(authRegistrationDto);

        mockMvc.perform(post("/registrations")
                        .content(objectMapper.writeValueAsString(newRegistrationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id", is(authRegistrationDto.getId()), Long.class))
                .andExpect(jsonPath("$.password").exists())
                .andExpect(jsonPath("$.password", is(authRegistrationDto.getPassword())));
    }

    @Test
    public void createRegistration_whenNameIsNull_thenThrowException() throws Exception {
        NewRegistrationDto newRegistrationDto = NewRegistrationDto.builder()
                .email("yury@yandex.ru")
                .phone("+79991234567")
                .eventId(1L).build();

        AuthRegistrationDto authRegistrationDto = AuthRegistrationDto.builder()
                .id(1L).password("1234").build();

        when(registrationService.createRegistration(any(NewRegistrationDto.class))).thenReturn(authRegistrationDto);

        mockMvc.perform(post("/registrations")
                        .content(objectMapper.writeValueAsString(newRegistrationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createRegistration_whenNameIsBlank_thenThrowException() throws Exception {
        NewRegistrationDto newRegistrationDto = NewRegistrationDto.builder()
                .name("    ")
                .email("yury@yandex.ru")
                .phone("+79991234567")
                .eventId(1L).build();

        AuthRegistrationDto authRegistrationDto = AuthRegistrationDto.builder()
                .id(1L).password("1234").build();

        when(registrationService.createRegistration(any(NewRegistrationDto.class))).thenReturn(authRegistrationDto);

        mockMvc.perform(post("/registrations")
                        .content(objectMapper.writeValueAsString(newRegistrationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createRegistration_whenNameIsTooShort_thenThrowException() throws Exception {
        NewRegistrationDto newRegistrationDto = NewRegistrationDto.builder()
                .name("Y")
                .email("yury@yandex.ru")
                .phone("+79991234567")
                .eventId(1L).build();

        AuthRegistrationDto authRegistrationDto = AuthRegistrationDto.builder()
                .id(1L).password("1234").build();

        when(registrationService.createRegistration(any(NewRegistrationDto.class))).thenReturn(authRegistrationDto);

        mockMvc.perform(post("/registrations")
                        .content(objectMapper.writeValueAsString(newRegistrationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createRegistration_whenEmailIsNull_thenThrowException() throws Exception {
        NewRegistrationDto newRegistrationDto = NewRegistrationDto.builder()
                .name("Yury")
                .phone("+79991234567")
                .eventId(1L).build();

        AuthRegistrationDto authRegistrationDto = AuthRegistrationDto.builder()
                .id(1L).password("1234").build();

        when(registrationService.createRegistration(any(NewRegistrationDto.class))).thenReturn(authRegistrationDto);

        mockMvc.perform(post("/registrations")
                        .content(objectMapper.writeValueAsString(newRegistrationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createRegistration_whenEmailIsInvalid_thenThrowException() throws Exception {
        NewRegistrationDto newRegistrationDto = NewRegistrationDto.builder()
                .name("Yury")
                .email("yury.yandex.ru")
                .phone("+79991234567")
                .eventId(1L).build();

        AuthRegistrationDto authRegistrationDto = AuthRegistrationDto.builder()
                .id(1L).password("1234").build();

        when(registrationService.createRegistration(any(NewRegistrationDto.class))).thenReturn(authRegistrationDto);

        mockMvc.perform(post("/registrations")
                        .content(objectMapper.writeValueAsString(newRegistrationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createRegistration_whenPhoneIsNull_thenThrowException() throws Exception {
        NewRegistrationDto newRegistrationDto = NewRegistrationDto.builder()
                .name("Yury")
                .email("yury@yandex.ru")
                .eventId(1L).build();

        AuthRegistrationDto authRegistrationDto = AuthRegistrationDto.builder()
                .id(1L).password("1234").build();

        when(registrationService.createRegistration(any(NewRegistrationDto.class))).thenReturn(authRegistrationDto);

        mockMvc.perform(post("/registrations")
                        .content(objectMapper.writeValueAsString(newRegistrationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createRegistration_whenPhoneIsInvalid_thenThrowException() throws Exception {
        NewRegistrationDto newRegistrationDto = NewRegistrationDto.builder()
                .name("Yury")
                .email("yury@yandex.ru")
                .phone("+7999number")
                .eventId(1L).build();

        AuthRegistrationDto authRegistrationDto = AuthRegistrationDto.builder()
                .id(1L).password("1234").build();

        when(registrationService.createRegistration(any(NewRegistrationDto.class))).thenReturn(authRegistrationDto);

        mockMvc.perform(post("/registrations")
                        .content(objectMapper.writeValueAsString(newRegistrationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createRegistration_whenEventIdIsNull_thenThrowException() throws Exception {
        NewRegistrationDto newRegistrationDto = NewRegistrationDto.builder()
                .name("Yury")
                .email("yury@yandex.ru")
                .phone("+79991234567").build();

        AuthRegistrationDto authRegistrationDto = AuthRegistrationDto.builder()
                .id(1L).password("1234").build();

        when(registrationService.createRegistration(any(NewRegistrationDto.class))).thenReturn(authRegistrationDto);

        mockMvc.perform(post("/registrations")
                        .content(objectMapper.writeValueAsString(newRegistrationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // Method "updateRegistrationData" tests.
    @Test
    public void updateRegistrationData_whenInputValid_thenUpdate() throws Exception {
        UpdateRegistrationDto updateRegistrationDto = UpdateRegistrationDto.builder()
                .id(1L)
                .password("1234")
                .name("Yuri")
                .email("yuri@yandex.ru")
                .phone("+79991234560").build();

        PublicRegistrationDto publicRegistrationDto = PublicRegistrationDto.builder()
                .name("Yuri")
                .email("yuri@yandex.ru")
                .phone("+79991234560")
                .eventId(1L).build();

        when(registrationService.updateRegistrationData(any(UpdateRegistrationDto.class))).thenReturn(publicRegistrationDto);

        mockMvc.perform(patch("/registrations")
                        .content(objectMapper.writeValueAsString(updateRegistrationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.name", is(publicRegistrationDto.getName())))
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.email", is(publicRegistrationDto.getEmail())))
                .andExpect(jsonPath("$.phone").exists())
                .andExpect(jsonPath("$.phone", is(publicRegistrationDto.getPhone())))
                .andExpect(jsonPath("$.eventId").exists())
                .andExpect(jsonPath("$.eventId", is(publicRegistrationDto.getEventId()), Long.class))
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    public void updateRegistrationData_whenRegistrationNotExists_thenThrowException() throws Exception {
        UpdateRegistrationDto updateRegistrationDto = UpdateRegistrationDto.builder()
                .id(1L)
                .password("1234")
                .name("Yuri")
                .email("yuri@yandex.ru")
                .phone("+79991234560").build();

        when(registrationService.updateRegistrationData(any(UpdateRegistrationDto.class)))
                .thenThrow(new EntityNotFoundException("Registration doesn't exist."));

        mockMvc.perform(patch("/registrations")
                        .content(objectMapper.writeValueAsString(updateRegistrationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateRegistrationData_whenPasswordIsInvalid_thenThrowException() throws Exception {
        UpdateRegistrationDto updateRegistrationDto = UpdateRegistrationDto.builder()
                .id(1L)
                .password("1234")
                .name("Yuri")
                .email("yuri@yandex.ru")
                .phone("+79991234560").build();

        when(registrationService.updateRegistrationData(any(UpdateRegistrationDto.class)))
                .thenThrow(new AuthenticationException("Incorrect password."));

        mockMvc.perform(patch("/registrations")
                        .content(objectMapper.writeValueAsString(updateRegistrationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updateRegistrationData_whenIdIsNull_thenThrowException() throws Exception {
        UpdateRegistrationDto updateRegistrationDto = UpdateRegistrationDto.builder()
                .password("1234")
                .name("Yuri")
                .email("yuri@yandex.ru")
                .phone("+79991234560").build();

        mockMvc.perform(patch("/registrations")
                        .content(objectMapper.writeValueAsString(updateRegistrationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateRegistrationData_whenIdInvalid_thenThrowException() throws Exception {
        UpdateRegistrationDto updateRegistrationDto = UpdateRegistrationDto.builder()
                .id(0L)
                .password("1234")
                .name("Yuri")
                .email("yuri@yandex.ru")
                .phone("+79991234560").build();

        mockMvc.perform(patch("/registrations")
                        .content(objectMapper.writeValueAsString(updateRegistrationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateRegistrationData_whenPasswordIsNull_thenThrowException() throws Exception {
        UpdateRegistrationDto updateRegistrationDto = UpdateRegistrationDto.builder()
                .id(1L)
                .name("Yuri")
                .email("yuri@yandex.ru")
                .phone("+79991234560").build();

        mockMvc.perform(patch("/registrations")
                        .content(objectMapper.writeValueAsString(updateRegistrationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateRegistrationData_whenNameIsBlank_thenThrowException() throws Exception {
        UpdateRegistrationDto updateRegistrationDto = UpdateRegistrationDto.builder()
                .id(1L)
                .password("1234")
                .name("    ").build();

        mockMvc.perform(patch("/registrations")
                        .content(objectMapper.writeValueAsString(updateRegistrationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateRegistrationData_whenNameIsTooShort_thenThrowException() throws Exception {
        UpdateRegistrationDto updateRegistrationDto = UpdateRegistrationDto.builder()
                .id(1L)
                .password("1234")
                .name("Y").build();

        mockMvc.perform(patch("/registrations")
                        .content(objectMapper.writeValueAsString(updateRegistrationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateRegistrationData_whenEmailIsInvalid_thenThrowException() throws Exception {
        UpdateRegistrationDto updateRegistrationDto = UpdateRegistrationDto.builder()
                .id(1L)
                .password("1234")
                .email("yuri.yandex.ru").build();

        mockMvc.perform(patch("/registrations")
                        .content(objectMapper.writeValueAsString(updateRegistrationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateRegistrationData_whenPhoneIsInvalid_thenThrowException() throws Exception {
        UpdateRegistrationDto updateRegistrationDto = UpdateRegistrationDto.builder()
                .id(1L)
                .password("1234")
                .phone("+7999number").build();

        mockMvc.perform(patch("/registrations")
                        .content(objectMapper.writeValueAsString(updateRegistrationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // Method "deleteRegistration" tests.
    @Test
    public void deleteRegistration_whenInputValid_thenDelete() throws Exception {
        AuthRegistrationDto authRegistrationDto = AuthRegistrationDto.builder()
                .id(1L).password("1234").build();

        mockMvc.perform(delete("/registrations")
                        .content(objectMapper.writeValueAsString(authRegistrationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteRegistration_whenRegistrationNotExists_thenThrowException() throws Exception {
        AuthRegistrationDto authRegistrationDto = AuthRegistrationDto.builder()
                .id(1L).password("1234").build();

        doThrow(new EntityNotFoundException("Registration doesn't exist."))
                .when(registrationService).deleteRegistration(any(AuthRegistrationDto.class));

        mockMvc.perform(delete("/registrations")
                        .content(objectMapper.writeValueAsString(authRegistrationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteRegistration_whenIdInvalid_thenThrowException() throws Exception {
        AuthRegistrationDto authRegistrationDto = AuthRegistrationDto.builder()
                .id(0L).password("1234").build();

        mockMvc.perform(delete("/registrations")
                        .content(objectMapper.writeValueAsString(authRegistrationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteRegistration_whenPasswordIsInvalid_thenThrowException() throws Exception {
        AuthRegistrationDto authRegistrationDto = AuthRegistrationDto.builder()
                .id(1L).password("1234").build();

        doThrow(new AuthenticationException("Password is incorrect."))
                .when(registrationService).deleteRegistration(any(AuthRegistrationDto.class));

        mockMvc.perform(delete("/registrations")
                        .content(objectMapper.writeValueAsString(authRegistrationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    // Method "getRegistration" tests.
    @Test
    public void getRegistration_whenInputValid_thenReturn() throws Exception {
        PublicRegistrationDto publicRegistrationDto = PublicRegistrationDto.builder()
                .name("Yuri")
                .email("yuri@yandex.ru")
                .phone("+79991234560")
                .eventId(1L).build();

        Long registrationId = 1L;

        when(registrationService.getRegistration(registrationId)).thenReturn(publicRegistrationDto);

        mockMvc.perform(get("/registrations/{registrationId}", registrationId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.name", is(publicRegistrationDto.getName())))
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.email", is(publicRegistrationDto.getEmail())))
                .andExpect(jsonPath("$.phone").exists())
                .andExpect(jsonPath("$.phone", is(publicRegistrationDto.getPhone())))
                .andExpect(jsonPath("$.eventId").exists())
                .andExpect(jsonPath("$.eventId", is(publicRegistrationDto.getEventId()), Long.class))
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    public void getRegistration_whenRegistrationNotExists_thenThrowException() throws Exception {
        Long registrationId = 1L;

        when(registrationService.getRegistration(registrationId))
                .thenThrow(new EntityNotFoundException("Registration doesn't exist."));

        mockMvc.perform(get("/registrations/{registrationId}", registrationId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getRegistration_whenIdInvalid_thenThrowException() throws Exception {
        Long registrationId = 0L;

        mockMvc.perform(get("/registrations/{registrationId}", registrationId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    // Method "getRegistrations" tests.

    @Test
    public void getRegistrations_whenInputValid_thenReturn() throws Exception {
        Long eventId = 1L;

        List<PublicRegistrationDto> publicRegistrationDtoList = List.of(
                PublicRegistrationDto.builder()
                        .name("Yuri")
                        .email("yuri@yandex.ru")
                        .phone("+79991234560")
                        .eventId(1L).build(),
                PublicRegistrationDto.builder()
                        .name("Igor")
                        .email("igor@yandex.ru")
                        .phone("+79991234561")
                        .eventId(1L).build()
        );


        when(registrationService.getRegistrations(any(Long.class), any(Pageable.class)))
                .thenReturn(publicRegistrationDtoList);

        mockMvc.perform(get("/registrations")
                        .param("eventId", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].name", is(publicRegistrationDtoList.get(0).getName())))
                .andExpect(jsonPath("$[0].email").exists())
                .andExpect(jsonPath("$[0].email", is(publicRegistrationDtoList.get(0).getEmail())))
                .andExpect(jsonPath("$[0].phone").exists())
                .andExpect(jsonPath("$[0].phone", is(publicRegistrationDtoList.get(0).getPhone())))
                .andExpect(jsonPath("$[0].eventId").exists())
                .andExpect(jsonPath("$[0].eventId", is(publicRegistrationDtoList.get(0).getEventId()), Long.class))
                .andExpect(jsonPath("$[0].id").doesNotExist())
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[1].name", is(publicRegistrationDtoList.get(1).getName())))
                .andExpect(jsonPath("$[1].email").exists())
                .andExpect(jsonPath("$[1].email", is(publicRegistrationDtoList.get(1).getEmail())))
                .andExpect(jsonPath("$[1].phone").exists())
                .andExpect(jsonPath("$[1].phone", is(publicRegistrationDtoList.get(1).getPhone())))
                .andExpect(jsonPath("$[1].eventId").exists())
                .andExpect(jsonPath("$[1].eventId", is(publicRegistrationDtoList.get(1).getEventId()), Long.class))
                .andExpect(jsonPath("$[1].id").doesNotExist())
                .andExpect(jsonPath("$[1].password").doesNotExist());
    }

    @Test
    public void getRegistrations_whenNoEventIdParam_thenThrowException() throws Exception {
        mockMvc.perform(get("/registrations")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getRegistrations_whenEventIdParamInvalid_thenThrowException() throws Exception {
        mockMvc.perform(get("/registrations")
                        .param("eventId", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void testUpdateRegistrationStatus_Success() throws Exception {
        UpdateStatusDto updateStatusDto = new UpdateStatusDto(1L, "APPROVED", "");
        PublicRegistrationStatusDto responseDto = new PublicRegistrationStatusDto("name", "email",
                "+79991234560",1L, "APPROVED",
                LocalDateTime.of(2024, 11, 27, 8, 0), "");

        when(registrationService.updateRegistrationStatus(updateStatusDto)).thenReturn(responseDto);

        mockMvc.perform(patch("/registrations/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStatusDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(1L))
                .andExpect(jsonPath("$.registrationStatus").value("APPROVED"));
    }

    @Test
    void testUpdateRegistrationStatus_InvalidInput() throws Exception {
        UpdateStatusDto invalidDto = new UpdateStatusDto(null, "", ""); // Invalid data

        mockMvc.perform(patch("/registrations/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetRegistrationsByStatusAndEventId_Success() throws Exception {
        List<PublicRegistrationStatusDto> response = List.of(
                new PublicRegistrationStatusDto("name1", "email1",
                        "+79991234560",1L, "APPROVED",
                        LocalDateTime.of(2024, 11, 27, 8, 0), ""),
                new PublicRegistrationStatusDto("name2", "email2",
                        "+79991234561",1L, "PENDING",
                        LocalDateTime.of(2024, 11, 27, 9, 0), "")
        );

        when(registrationService.getRegistrationsWithStatusesAndEventId(1L, List.of("APPROVED", "PENDING")))
                .thenReturn(response);

        mockMvc.perform(get("/registrations/status/1")
                        .param("status", "APPROVED")
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventId").value(1L))
                .andExpect(jsonPath("$[0].registrationStatus").value("APPROVED"))
                .andExpect(jsonPath("$[0].name").value("name1"))
                .andExpect(jsonPath("$[1].eventId").value(1L))
                .andExpect(jsonPath("$[1].registrationStatus").value("PENDING"))
                .andExpect(jsonPath("$[1].name").value("name2"));
    }

    @Test
    void testGetRegistrationsByStatusAndEventId_InvalidEventId() throws Exception {
        mockMvc.perform(get("/registrations/status/0") // Invalid eventId
                        .param("status", "APPROVED"))
                .andExpect(status().isConflict());
    }

    @Test
    void testGetRegistrationsByStatusAndEventId_NoStatuses() throws Exception {
        mockMvc.perform(get("/registrations/status/1")) // Missing statuses
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCountByStatus_Success() throws Exception {
        Map<String, Long> response = Map.of("APPROVED", 10L, "PENDING", 5L);

        when(registrationService.countRegistrationsByStatus(1L)).thenReturn(response);

        mockMvc.perform(get("/registrations/status/count")
                        .param("eventId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.APPROVED").value(10L))
                .andExpect(jsonPath("$.PENDING").value(5L));
    }

    @Test
    void testCountByStatus_InvalidEventId() throws Exception {
        mockMvc.perform(get("/registrations/status/count")
                        .param("eventId", "0")) // Invalid eventId
                .andExpect(status().isConflict());
    }

    @Test
    void testCountByStatus_EmptyResponse() throws Exception {
        when(registrationService.countRegistrationsByStatus(1L)).thenReturn(Collections.emptyMap());

        mockMvc.perform(get("/registrations/status/count")
                        .param("eventId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

}
