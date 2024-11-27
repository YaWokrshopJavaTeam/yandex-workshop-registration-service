package ru.practicum.workshop.registrationservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PublicRegistrationStatusDto {

    private String name;

    private String email;

    private String phone;

    private Long eventId;

    private String registrationStatus;

    private LocalDateTime createdAt;

    private String reason;
}
