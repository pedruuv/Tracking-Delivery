package com.tracking_delivery_system.tracking_service.repository;

import com.tracking_delivery_system.tracking_service.model.LocationUpdate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LocationUpdateRepository extends JpaRepository<LocationUpdate, UUID> {
}
