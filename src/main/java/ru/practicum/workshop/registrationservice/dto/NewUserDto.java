package ru.practicum.workshop.registrationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDto {
    private String name;
    private String email;
    private String password;
    private String aboutMe;
}
