package ru.practicum.workshop.registrationservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static ru.practicum.workshop.registrationservice.dto.RegistrationDtoValidationConstants.ID_NOT_NULL_ERROR_MESSAGE;
import static ru.practicum.workshop.registrationservice.dto.RegistrationDtoValidationConstants.STATUS_NOT_NULL_ERROR_MESSAGE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusDto {
    @NotNull(message = ID_NOT_NULL_ERROR_MESSAGE)
    private Long id;

    @NotNull(message = STATUS_NOT_NULL_ERROR_MESSAGE)
    private String status;

    private String reason;
}
