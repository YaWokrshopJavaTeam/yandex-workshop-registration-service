package ru.practicum.workshop.registrationservice.mapping;

import org.mapstruct.*;
import ru.practicum.workshop.registrationservice.dto.*;
import ru.practicum.workshop.registrationservice.model.Registration;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {

    Registration toRegistration(NewRegistrationDto newRegistrationDto, String password, String registrationStatus,
                                LocalDateTime createdAt);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Registration updateRegistrationData(@MappingTarget Registration registration, UpdateRegistrationDto updateRegistrationDto);

    AuthRegistrationDto toAuthRegistrationDto(Registration registration);

    PublicRegistrationDto toPublicRegistrationDto(Registration registration);

    List<PublicRegistrationDto> toPublicRegistrationDto(List<Registration> registrationList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PublicRegistrationStatusDto toStatusRegistrationDtoWithoutReason(Registration registration);

    @Mapping(target = "reason", source = "ownerReason")
    PublicRegistrationStatusDto toStatusRegistrationDtoWithReason(Registration registration, String ownerReason);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    List<PublicRegistrationStatusDto> toListStatusRegistrationDto(List<Registration> registrations);
}
