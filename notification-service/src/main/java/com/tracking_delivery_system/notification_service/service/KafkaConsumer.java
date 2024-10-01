package com.tracking_delivery_system.notification_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracking_delivery_system.notification_service.model.LocationUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class KafkaConsumer {
    private final Map<String, Object> messageBuffer = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "location-update", groupId = "notification-service")
    public void consumeTrackingUpdate(String trackingUpdate) {
        try {
            LocationUpdate locationUpdate = mapper.readValue(trackingUpdate, LocationUpdate.class);
            messageBuffer.put("location-update", locationUpdate);
            checkAndPrintMessage();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "delivery-status", groupId = "notification-group")
    public void consumeDeliveryStatusUpdate(String statusUpdate){
        messageBuffer.put("delivery-status", statusUpdate);
        checkAndPrintMessage();
    }

    private synchronized void checkAndPrintMessage() {
        if (messageBuffer.containsKey("location-update") && messageBuffer.containsKey("delivery-status")){
            LocationUpdate locationUpdate = (LocationUpdate) messageBuffer.get("location-update");
            String status = (String) messageBuffer.get("delivery-status");

            log.info("Delivery id: {}, Remaining distance: {}, Status: {}", locationUpdate.getId(), locationUpdate.getDistance(), status);

            messageBuffer.clear();
        }
    }
}
