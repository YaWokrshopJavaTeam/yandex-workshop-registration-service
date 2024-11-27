package ru.practicum.workshop.registrationservice.model;

import jakarta.validation.ValidationException;

public enum RegistrationStatus {
    PENDING,
    APPROVED,
    WAITING,
    REJECTED;

    public static RegistrationStatus parseStatus(String str) {
        RegistrationStatus registrationStatus;
        if (str == null) {
            registrationStatus = RegistrationStatus.PENDING;
        } else {
            try {
                registrationStatus = RegistrationStatus.valueOf(str);
            } catch (IllegalArgumentException e) {
                throw new ValidationException(String.format("Unknown status: %s", str));
            }
        }
        return registrationStatus;
    }
}
