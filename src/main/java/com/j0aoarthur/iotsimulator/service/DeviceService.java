package com.j0aoarthur.iotsimulator.service;

import com.j0aoarthur.iotsimulator.dto.DeviceRequest;
import com.j0aoarthur.iotsimulator.entity.Device;
import com.j0aoarthur.iotsimulator.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository repository;

    public Device registerDevice(DeviceRequest request) {
        Device device = Device.builder()
                .userId(request.userId())
                .name(request.name())
                .location(request.location())
                .baseValue(request.baseValue())
                .build();
        return repository.save(device);
    }

    public List<Device> getAllDevices() {
        return repository.findAll();
    }
}
