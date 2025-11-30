package com.j0aoarthur.iotsimulator.repository;

import com.j0aoarthur.iotsimulator.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    // useful for fetching alerts for a specific device
    List<Alert> findByDeviceId(UUID deviceId);
}
