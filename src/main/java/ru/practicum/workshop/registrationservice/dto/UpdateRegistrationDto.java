package ru.practicum.workshop.registrationservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static ru.practicum.workshop.registrationservice.dto.RegistrationDtoValidationConstants.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRegistrationDto {

    @NotNull(message = ID_NOT_NULL_ERROR_MESSAGE)
    private Long id;

    @NotNull(message = PASSWORD_NOT_NULL_ERROR_MESSAGE)
    private String password;

    @Pattern(regexp = NAME_PATTERN_REGEXP, message = NAME_PATTERN_ERROR_MESSAGE)
    @Size(min = NAME_SIZE_MIN, max = NAME_SIZE_MAX, message = NAME_SIZE_ERROR_MESSAGE)
    private String name;

    @Email(message = EMAIL_EMAIL_ERROR_MESSAGE)
    private String email;

    @Pattern(regexp = PHONE_PATTERN_REGEXP, message = PHONE_PATTERN_ERROR_MESSAGE)
    private String phone;

}
