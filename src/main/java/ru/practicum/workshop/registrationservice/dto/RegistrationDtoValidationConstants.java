package ru.practicum.workshop.registrationservice.dto;

public class RegistrationDtoValidationConstants {

    public static final String ID_NOT_NULL_ERROR_MESSAGE = "Registration id must be non-null.";

    public static final String PASSWORD_NOT_NULL_ERROR_MESSAGE = "Registration password must be non-null.";

    public static final String NAME_PATTERN_REGEXP = "^\\s*\\S.*";
    public static final String NAME_PATTERN_ERROR_MESSAGE = "User name must be non-blank";
    public static final String NAME_NOT_BLANK_ERROR_MESSAGE = "User name must be non-blank.";
    public static final int NAME_SIZE_MIN = 3;
    public static final int NAME_SIZE_MAX = 64;
    public static final String NAME_SIZE_ERROR_MESSAGE =
            "User name length must be between " + NAME_SIZE_MIN + " and " + NAME_SIZE_MAX + ".";

    public static final String EMAIL_NOT_NULL_ERROR_MESSAGE = "User email must be non-null.";
    public static final String EMAIL_EMAIL_ERROR_MESSAGE = "User email must be valid.";

    public static final String PHONE_NOT_NULL_ERROR_MESSAGE = "User phone number must be non-null.";
    public static final String PHONE_PATTERN_REGEXP = "^\\+\\d+";
    public static final String PHONE_PATTERN_ERROR_MESSAGE =
            "User phone number must start with + following with digits.";

    public static final String EVENT_ID_NOT_NULL_ERROR_MESSAGE = "Event id must be non-null.";

}