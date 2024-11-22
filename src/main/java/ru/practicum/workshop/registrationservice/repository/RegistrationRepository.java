package ru.practicum.workshop.registrationservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.workshop.registrationservice.model.Registration;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    List<Registration> findAllByEventId(Long eventId, Pageable pageable);

}
