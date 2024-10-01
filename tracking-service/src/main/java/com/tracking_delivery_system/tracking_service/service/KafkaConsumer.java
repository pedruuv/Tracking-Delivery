package com.tracking_delivery_system.tracking_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracking_delivery_system.tracking_service.model.LocationUpdate;
import com.tracking_delivery_system.tracking_service.repository.LocationUpdateRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private final LocationUpdateRepository repository;
    private final DeliveryProgressSimulator simulator;
    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "new-delivery", groupId = "tracking-group")
    public void consumeNewDelivery(String newDelivery) {
        try {
            LocationUpdate locationUpdate = mapper.readValue(newDelivery, LocationUpdate.class);
            repository.save(locationUpdate);
            simulator.simulateDeliverProgress(locationUpdate);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    public void recoverPendingDeliveries() {
        repository.findAll().forEach(d -> {
            if (d.getDistance() > 0) {
                simulator.simulateDeliverProgress(d);
            }
        });
    }
}
