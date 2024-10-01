package com.tracking_delivery_system.delivery_status_service.repository;

import com.tracking_delivery_system.delivery_status_service.model.Deliver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeliverRepository extends JpaRepository<Deliver, UUID> {
}
