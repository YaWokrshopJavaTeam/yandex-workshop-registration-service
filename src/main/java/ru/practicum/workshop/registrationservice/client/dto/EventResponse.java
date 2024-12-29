package ru.practicum.workshop.registrationservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String location;
    private Long ownerId;
    private LocalDateTime createdDateTime;
    private EventRegistrationStatus registrationStatus;
    private boolean isLimited;
    private Integer participantLimit;
}
