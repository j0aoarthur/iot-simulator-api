package com.j0aoarthur.iotsimulator.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "devices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID deviceId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String name;

    private String location;

    // Consumo base (referência de quanto o aparelho gasta por ciclo)
    @Column(name = "base_value", nullable = false)
    private Double baseValue;

    // NOVO CAMPO: O "relógio" acumulado do dispositivo
    // Inicia com 0.0 se não for informado
    @Builder.Default
    @Column(name = "total_consumption", nullable = false)
    private Double totalConsumption = 0.0;
}