package ru.practicum.workshop.registrationservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static ru.practicum.workshop.registrationservice.dto.RegistrationDtoValidationConstants.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRegistrationDto {

    @NotNull(message = ID_NOT_NULL_ERROR_MESSAGE)
    @Positive
    private Long id;

    @NotNull(message = PASSWORD_NOT_NULL_ERROR_MESSAGE)
    private String password;

}
