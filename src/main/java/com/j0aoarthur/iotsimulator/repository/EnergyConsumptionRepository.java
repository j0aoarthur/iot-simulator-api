package com.j0aoarthur.iotsimulator.repository;
;
import com.j0aoarthur.iotsimulator.entity.EnergyConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface EnergyConsumptionRepository extends JpaRepository<EnergyConsumption, Long> {

    // useful for fetching history for a specific device
    List<EnergyConsumption> findByDeviceId(UUID deviceId);
}
