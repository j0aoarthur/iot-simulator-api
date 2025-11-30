package com.j0aoarthur.iotsimulator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record DeviceRequest(
        @NotNull(message = "User ID não pode ser nulo")
        UUID userId,

        @NotBlank(message = "Nome do dispositivo não pode ser vazio")
        String name,

        String location,

        @NotNull(message = "Valor base não pode ser nulo")
        @Positive(message = "Consumo base deve ser um valor positivo")
        Double baseValue
) {}