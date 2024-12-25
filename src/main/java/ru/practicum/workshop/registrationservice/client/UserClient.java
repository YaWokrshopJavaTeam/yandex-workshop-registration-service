package ru.practicum.workshop.registrationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.practicum.workshop.registrationservice.client.config.CustomFeignClientConfiguration;
import ru.practicum.workshop.registrationservice.dto.NewUserDto;
import ru.practicum.workshop.registrationservice.client.dto.UpdateUserFromRegistrationDto;

@FeignClient(name = "user-service-client", url = "http://host.docker.internal:8081",
        configuration = CustomFeignClientConfiguration.class
)
public interface UserClient {
    @PostMapping("/users/internal")
    Long autoCreateUser(@RequestBody NewUserDto newUserDto);

    @PatchMapping("/users/internal")
    void autoUpdateUser(@RequestBody UpdateUserFromRegistrationDto updateUserFromRegistrationDto,
                        @RequestHeader("X-User-Id") Long userId);

    @DeleteMapping("/users/internal")
    void autoDeleteUser(@RequestHeader("X-User-Id") Long userId);

}
