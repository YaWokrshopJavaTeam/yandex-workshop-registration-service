package ru.practicum.workshop.registrationservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.workshop.registrationservice.model.Registration;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    List<Registration> findAllByEventId(Long eventId, Pageable pageable);

    Optional<Registration> findFirstByRegistrationStatusOrderByCreatedAtAsc(String status);

    List<Registration> findAllByEventIdAndRegistrationStatusInOrderByCreatedAt(Long eventId, List<String> statuses);

    @Query(value ="SELECT registration_status, COUNT(*) FROM registrations WHERE event_id = :eventId group by registration_status",
            nativeQuery = true)
    List<Object[]> getListByEventIdAndGroupByRegistrationStatus(Long eventId);
}
