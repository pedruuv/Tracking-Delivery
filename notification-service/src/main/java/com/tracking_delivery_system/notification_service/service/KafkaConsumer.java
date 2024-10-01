package com.tracking_delivery_system.notification_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracking_delivery_system.notification_service.model.LocationUpdate;
import com.tracking_delivery_system.notification_service.model.StatusUpdate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class KafkaConsumer {
    private final Map<String, Map<String, Object>> deliveriesMap = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "location-update", groupId = "notification-service")
    public void consumeTrackingUpdate(String trackingUpdate) {
        try {
            LocationUpdate locationUpdate = mapper.readValue(trackingUpdate, LocationUpdate.class);
            String deliveryId = locationUpdate.getId().toString();
            
            deliveriesMap.computeIfAbsent(deliveryId, id -> new HashMap<>());
            deliveriesMap.get(deliveryId).put("location-update", locationUpdate);

            checkAndPrintMessage(deliveryId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "delivery-status", groupId = "notification-group")
    public void consumeDeliveryStatusUpdate(String statusUpdate) throws JsonMappingException, JsonProcessingException{
        StatusUpdate status = mapper.readValue(statusUpdate, StatusUpdate.class);
        String deliveryId = status.getId().toString();

        deliveriesMap.computeIfAbsent(deliveryId, id -> new HashMap<>());
        deliveriesMap.get(deliveryId).put("delivery-status", status);

        checkAndPrintMessage(deliveryId);
    }

    private synchronized void checkAndPrintMessage(String deliveryId) {
        Map<String, Object> deliveryMap = deliveriesMap.get(deliveryId);

        if (deliveryMap.containsKey("location-update") && deliveryMap.containsKey("delivery-status")) {
            LocationUpdate locationUpdate = (LocationUpdate) deliveryMap.get("location-update");
            StatusUpdate statusUpdate = (StatusUpdate) deliveryMap.get("delivery-status");
            String status = statusUpdate.getStatus();

            log.info("Delivery id: {}, Remaining distance: {}, Status: {}", locationUpdate.getId(), locationUpdate.getDistance(), status);
            
        }
        }
    }
