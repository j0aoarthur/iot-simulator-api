package com.j0aoarthur.iotsimulator.service;

import com.j0aoarthur.iotsimulator.entity.Alert;
import com.j0aoarthur.iotsimulator.entity.Device;
import com.j0aoarthur.iotsimulator.entity.EnergyConsumption;
import com.j0aoarthur.iotsimulator.repository.AlertRepository;
import com.j0aoarthur.iotsimulator.repository.DeviceRepository;
import com.j0aoarthur.iotsimulator.repository.EnergyConsumptionRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class SimulationService {

    private final DeviceRepository deviceRepository;
    private final EnergyConsumptionRepository consumptionRepository;
    private final AlertRepository alertRepository;
    private final Random random = new Random();

    private final Counter readingsCounter;
    private final Counter alertsCounter;

    @Value("${app.simulation.anomaly-threshold}")
    private double anomalyThreshold;

    public SimulationService(DeviceRepository deviceRepository,
                             EnergyConsumptionRepository consumptionRepository,
                             AlertRepository alertRepository,
                             MeterRegistry registry) {
        this.deviceRepository = deviceRepository;
        this.consumptionRepository = consumptionRepository;
        this.alertRepository = alertRepository;
        this.readingsCounter = Counter.builder("iot.readings.total").register(registry);
        this.alertsCounter = Counter.builder("iot.alerts.total").register(registry);
    }

    @Scheduled(fixedRateString = "${app.simulation.rate-ms}")
    public void generateSensorData() {
        List<Device> devices = deviceRepository.findAll();
        if (devices.isEmpty()) return;

        log.info("Simulando dados para {} dispositivos...", devices.size());

        for (Device device : devices) {
            // 1. Calcular o valor instantâneo (Leitura atual)
            double base = device.getBaseValue();
            double fluctuation = 0.8 + (0.4 * random.nextDouble()); // Varia entre 0.8x e 1.2x
            boolean isSpike = random.nextDouble() > 0.95; // 5% de chance de pico

            double instantValue = base * fluctuation;
            if (isSpike) {
                instantValue = instantValue * 3.0;
            }

            // Arredondar para 2 casas
            instantValue = Math.round(instantValue * 100.0) / 100.0;

            // 2. Salvar o Histórico de Leitura (EnergyConsumption)
            EnergyConsumption reading = EnergyConsumption.builder()
                    .deviceId(device.getDeviceId())
                    .value(instantValue)
                    .timestamp(LocalDateTime.now())
                    .build();
            consumptionRepository.save(reading);
            readingsCounter.increment();

            // 3. ATUALIZAR O DISPOSITIVO (Somar ao Total)
            double newTotal = device.getTotalConsumption() + instantValue;
            // Arredondar o total também para não ficar com dizimas infinitas
            newTotal = Math.round(newTotal * 100.0) / 100.0;

            device.setTotalConsumption(newTotal);
            deviceRepository.save(device); // Salva o novo total no banco

            // 4. Checar Alertas
            if (isSpike || instantValue > anomalyThreshold) {
                Alert alert = Alert.builder()
                        .deviceId(device.getDeviceId())
                        .type("HIGH_USAGE_SPIKE")
                        .message("Pico detectado: " + instantValue + " kWh (Consumo total do dispositivo: " + newTotal + " kWh)")
                        .timestamp(LocalDateTime.now())
                        .build();
                alertRepository.save(alert);
                alertsCounter.increment();
                log.warn("🚨 Dispositivo {} Pico: {} kWh | Total Acumulado: {} kWh", device.getName(), instantValue, newTotal);
            }
        }
    }
}