package com.j0aoarthur.iotsimulator.controller;


import com.j0aoarthur.iotsimulator.dto.DeviceRequest;
import com.j0aoarthur.iotsimulator.entity.Alert;
import com.j0aoarthur.iotsimulator.entity.Device;
import com.j0aoarthur.iotsimulator.entity.EnergyConsumption;
import com.j0aoarthur.iotsimulator.repository.AlertRepository;
import com.j0aoarthur.iotsimulator.repository.EnergyConsumptionRepository;
import com.j0aoarthur.iotsimulator.service.DeviceService;
import com.j0aoarthur.iotsimulator.service.SimulationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class IotController {

    private final DeviceService deviceService;
    private final SimulationService simulationService;
    private final EnergyConsumptionRepository consumptionRepository;
    private final AlertRepository alertRepository;

    @PostMapping("/devices")
    public ResponseEntity<Device> registerDevice(@RequestBody @Valid DeviceRequest request) {
        return ResponseEntity.ok(deviceService.registerDevice(request));
    }

    @GetMapping("/devices")
    public ResponseEntity<List<Device>> getDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    @GetMapping("/devices/{deviceId}/readings")
    public ResponseEntity<List<EnergyConsumption>> getReadingsByDevice(@PathVariable UUID deviceId) {
        return ResponseEntity.ok(consumptionRepository.findByDeviceId(deviceId));
    }

    @GetMapping("/readings")
    public ResponseEntity<List<EnergyConsumption>> getReadings() {
        return ResponseEntity.ok(consumptionRepository.findAll());
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<Alert>> getAlerts() {
        return ResponseEntity.ok(alertRepository.findAll());
    }

    @PostMapping("/simulation/run")
    public ResponseEntity<String> forceSimulation() {
        simulationService.generateSensorData();
        return ResponseEntity.ok("Ciclo de simulação forçado com sucesso.");
    }
}
