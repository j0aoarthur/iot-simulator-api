package com.j0aoarthur.iotsimulator.repository;

import com.j0aoarthur.iotsimulator.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {}
