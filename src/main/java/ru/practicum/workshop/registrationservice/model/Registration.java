package ru.practicum.workshop.registrationservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "registrations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "registration_status")
    private String registrationStatus;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "password")
    private String password;
}
