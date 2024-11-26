package ru.practicum.workshop.registrationservice.mapping;

import org.mapstruct.*;
import ru.practicum.workshop.registrationservice.dto.*;
import ru.practicum.workshop.registrationservice.model.Registration;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {

    Registration toRegistration(NewRegistrationDto newRegistrationDto, String password);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
//    @Mapping(target = "", source = "registrationStatus", ignore = true)
//    @Mapping(target = "", source = "createdAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Registration updateRegistrationData(@MappingTarget Registration registration, UpdateRegistrationDto updateRegistrationDto);

//    @Mapping(target = "", source = "registrationStatus", ignore = true)
//    @Mapping(target = "", source = "createdAt", ignore = true)
    AuthRegistrationDto toAuthRegistrationDto(Registration registration);

//    @Mapping(target = "", source = "registrationStatus", ignore = true)
//    @Mapping(target = "", source = "createdAt", ignore = true)
    PublicRegistrationDto toPublicRegistrationDto(Registration registration);

//    @Mapping(target = "", source = "registrationStatus", ignore = true)
//    @Mapping(target = "", source = "createdAt", ignore = true)
    List<PublicRegistrationDto> toPublicRegistrationDto(List<Registration> registrationList);

//    @Mapping(target = "", source = "id", ignore = true)
//    @Mapping(target = "", source = "password", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PublicRegistrationStatusDto toStatusRegistrationDtoWithoutReason(Registration registration);

//    @Mapping(target = "", source = "id", ignore = true)
//    @Mapping(target = "", source = "password", ignore = true)
    @Mapping(target = "reason", source = "ownerReason")
    PublicRegistrationStatusDto toStatusRegistrationDtoWithReason(Registration registration, String ownerReason);

//    @Mapping(target = "", source = "id", ignore = true)
//    @Mapping(target = "", source = "password", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    List<PublicRegistrationStatusDto> toListStatusRegistrationDto(List<Registration> registrations);
}
