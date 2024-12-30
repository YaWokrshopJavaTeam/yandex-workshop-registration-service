package ru.practicum.workshop.registrationservice.client.dto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EventRegistrationStatus {
    OPEN("OPEN"),
    CLOSED("CLOSED"),
    SUSPENDED("SUSPENDED");
    private final String title;

    @Override
    public String toString() {
        return title;
    }
}
