package ru.practicum.workshop.registrationservice.model;

import jakarta.validation.ValidationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    public static boolean isTransitionValid(RegistrationStatus from, RegistrationStatus to) {
        return statusTransitionTable.get(from).contains(to);
    }

    private static final Map<RegistrationStatus, Set<RegistrationStatus>> statusTransitionTable = new HashMap<>();

    static {
        statusTransitionTable.put(PENDING, Set.of(APPROVED, REJECTED));
        statusTransitionTable.put(APPROVED, Set.of(WAITING));
        statusTransitionTable.put(WAITING, Set.of(APPROVED));
        statusTransitionTable.put(REJECTED, Set.of());
    }

}
