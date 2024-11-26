package ru.practicum.workshop.registrationservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.workshop.registrationservice.model.Registration;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    List<Registration> findAllByEventId(Long eventId, Pageable pageable);

    @Query(value = "SELECT * FROM registrations WHERE registration_status = :status ORDER BY created_at ASC LIMIT 1", nativeQuery = true)
    Optional<Registration> findEarliestRegistrationByStatusNative(@Param("status") String status);

    List<Registration> findAllByEventIdOrderByCreatedAt(Long eventId);

    Long countByEventIdAndRegistrationStatus(Long eventId, String status);
}
