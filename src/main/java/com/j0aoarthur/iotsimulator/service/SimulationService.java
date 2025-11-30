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

    // Alerta se a POTÊNCIA instantânea passar desse valor (em Watts)
    // Ex: Se algo consumir mais que 7000W, é um curto ou anomalia.
    @Value("${app.simulation.anomaly-threshold}")
    private double anomalyPowerThreshold; // Renomeie no application.yml ou use o valor antigo pensando em Watts

    @Value("${app.simulation.rate-ms}")
    private long rateMs; // Precisamos saber o intervalo para calcular o tempo

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

        for (Device device : devices) {
            // 1. Definição da Potência (Watts)
            double ratedPowerWatts = device.getBaseValue(); // Agora interpretamos como Watts!

            // Simulação de Estado (Ligado/Desligado)
            // Nem tudo fica ligado 100% do tempo na potência máxima.
            // Para simplificar, vamos variar a potência atual entre 10% (standby) e 110% (pico)
            double usageFactor = 0.1 + (1.0 * random.nextDouble());

            // Simulação de "Ciclo de Geladeira/Ar": Chance de estar no motor (alto) ou ventilador (baixo)
            // Se for potência alta (>1000W), chance de estar "desarmado" (apenas 5% da potência)
            if (ratedPowerWatts > 1000 && random.nextBoolean()) {
                usageFactor = 0.05; // Compressor desligado, só leds/sensores
            }

            double currentPowerWatts = ratedPowerWatts * usageFactor;

            // Spike (Curto-circuito ou partida de motor): 3x a potência nominal
            boolean isSpike = random.nextDouble() > 0.98;
            if (isSpike) {
                currentPowerWatts = ratedPowerWatts * 3.0;
            }

            // 2. A FÓRMULA MÁGICA: Converter Watts em kWh baseado no tempo decorrido
            // kWh = (Watts / 1000) * (Segundos / 3600)
            double timeInHours = (double) rateMs / 3600000.0; // converte ms para horas
            double energyConsumedKwh = (currentPowerWatts / 1000.0) * timeInHours;

            // Arredondar para 6 casas decimais (valores são pequenos agora)
            energyConsumedKwh = Math.round(energyConsumedKwh * 1000000.0) / 1000000.0;

            // 3. Salvar Leitura (kWh acumulado neste intervalo)
            EnergyConsumption reading = EnergyConsumption.builder()
                    .deviceId(device.getDeviceId())
                    .value(energyConsumedKwh)
                    .timestamp(LocalDateTime.now())
                    .build();
            consumptionRepository.save(reading);
            readingsCounter.increment();

            // 4. Atualizar Total do Dispositivo
            double newTotal = device.getTotalConsumption() + energyConsumedKwh;
            device.setTotalConsumption(newTotal);
            deviceRepository.save(device);

            // 5. Alerta (Baseado na Potência em Watts, não no kWh minúsculo)
            // Se a potência atual passar do limite (ex: 5000W) e for um spike
            if (isSpike && currentPowerWatts > anomalyPowerThreshold) {
                Alert alert = Alert.builder()
                        .deviceId(device.getDeviceId())
                        .type("HIGH_POWER_SURGE")
                        .message(String.format("Pico detectado: %.2f W (Potência nominal: %.0f W)", currentPowerWatts, ratedPowerWatts))
                        .timestamp(LocalDateTime.now())
                        .build();
                alertRepository.save(alert);
                alertsCounter.increment();
            }
        }
    }
}