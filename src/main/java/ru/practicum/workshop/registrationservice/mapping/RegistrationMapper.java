package ru.practicum.workshop.registrationservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.workshop.registrationservice.dto.AuthRegistrationDto;
import ru.practicum.workshop.registrationservice.dto.NewRegistrationDto;
import ru.practicum.workshop.registrationservice.dto.PublicRegistrationDto;
import ru.practicum.workshop.registrationservice.dto.UpdateRegistrationDto;
import ru.practicum.workshop.registrationservice.model.Registration;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {

    Registration toRegistration(NewRegistrationDto newRegistrationDto, String password);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    Registration updateRegistrationData(@MappingTarget Registration registration, UpdateRegistrationDto updateRegistrationDto);

    AuthRegistrationDto toAuthRegistrationDto(Registration registration);

    PublicRegistrationDto toPublicRegistrationDto(Registration registration);

    List<PublicRegistrationDto> toPublicRegistrationDto(List<Registration> registrationList);

}
