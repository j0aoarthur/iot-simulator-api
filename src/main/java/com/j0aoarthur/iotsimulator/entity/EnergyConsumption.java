package com.j0aoarthur.iotsimulator.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "energy_consumption")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnergyConsumption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID deviceId;

    @Column(name = "reading_value", nullable = false)
    private Double value; // kWh

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime timestamp;
}
