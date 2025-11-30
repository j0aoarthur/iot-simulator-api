package com.j0aoarthur.iotsimulator.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID deviceId;

    @Column(nullable = false)
    private String type; // e.g., SPIKE_DETECTED

    private String message;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime timestamp;
}
