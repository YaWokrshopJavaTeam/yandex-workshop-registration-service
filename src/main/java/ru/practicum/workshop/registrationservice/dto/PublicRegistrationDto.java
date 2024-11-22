package ru.practicum.workshop.registrationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicRegistrationDto {

    private String name;

    private String email;

    private String phone;

    private Long eventId;

}
