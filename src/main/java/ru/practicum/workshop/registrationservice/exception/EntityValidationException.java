package ru.practicum.workshop.registrationservice.exception;

public class EntityValidationException extends RuntimeException {

    public EntityValidationException(String message) {
        super(message);
    }
}
