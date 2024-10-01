package com.tracking_delivery_system.delivery_status_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracking_delivery_system.delivery_status_service.model.Deliver;
import com.tracking_delivery_system.delivery_status_service.model.LocationUpdate;
import com.tracking_delivery_system.delivery_status_service.repository.DeliverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private final DeliveryStatusUpdater statusUpdater;
    private final DeliverRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "location-update", groupId = "delivery-status")
    public void consumeLocationUpdate(String trackingUpdate) {
        try {
            LocationUpdate locationUpdate = mapper.readValue(trackingUpdate, LocationUpdate.class);
            Deliver deliver = repository.findById(locationUpdate.getId()).orElseThrow();
            statusUpdater.updateStatusBasedDistance(locationUpdate.getDistance(), deliver);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
