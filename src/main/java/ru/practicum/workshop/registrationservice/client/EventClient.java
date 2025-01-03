package ru.practicum.workshop.registrationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.practicum.workshop.registrationservice.client.config.CustomFeignClientConfiguration;
import ru.practicum.workshop.registrationservice.client.dto.EventResponse;
import ru.practicum.workshop.registrationservice.client.dto.PublicOrgTeamMemberDto;

import java.util.List;

@FeignClient(name = "event-service-client", url = "http://host.docker.internal:8082",
        configuration = CustomFeignClientConfiguration.class
)
public interface EventClient {
    @GetMapping("/events/{id}")
    EventResponse getEvent(@PathVariable Long id, @RequestHeader(value = "X-User-Id") Long requesterId);

    @GetMapping("/events/{id}")
    EventResponse getEvent(@PathVariable Long id);

    @GetMapping("/events/orgs/{eventId}")
    List<PublicOrgTeamMemberDto> getEventTeamMembers(@PathVariable Long eventId);
}
