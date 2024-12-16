package ru.practicum.workshop.registrationservice.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.workshop.registrationservice.dto.NewUserDto;
import ru.practicum.workshop.registrationservice.dto.ResponseWithUserId;

@FeignClient(name = "user-service-client", url = "http://localhost:8084")
public interface UserClient {
    @PostMapping("/users/internal")
    ResponseWithUserId autoCreateUser(@RequestBody NewUserDto newUserDto);
}
