package ru.practicum.workshop.registrationservice.exception;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ApiError {

    public String type;
    public String message;

}
