package com.tracking_delivery_system.delivery_status_service.service;

import com.tracking_delivery_system.delivery_status_service.model.Deliver;
import com.tracking_delivery_system.delivery_status_service.model.DeliveryStatus;
import com.tracking_delivery_system.delivery_status_service.model.StatusUpdate;
import com.tracking_delivery_system.delivery_status_service.repository.DeliverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class DeliveryStatusUpdater {
    private final KafkaProducer kafkaProducer;
    private final DeliverRepository repository;

    private static final double AWAITING_PICKUP_PERCENTAGE = 1;
    private static final double PICKED_UP_PERCENTAGE =  0.90;
    private static final double IN_TRANSIT_PERCENTAGE = 0.50;
    private static final double OUT_FOR_DELIVERY_PERCENTAGE = 0.10;
    private static final double DELIVERED_PERCENTAGE = 0;

    public void updateStatusBasedDistance(Long trackingProgress, Deliver deliver){
        long totalDistance = deliver.getDistance();
        UUID id = deliver.getId();
        double percentageRemaining = (double) trackingProgress / totalDistance;

        DeliveryStatus currentStatus = deliver.getStatus();
        DeliveryStatus newStatus = getNewStatusBasedOnPercentage(percentageRemaining, currentStatus);

        if (newStatus != currentStatus){
            updateDeliveryStatus(deliver, newStatus, id);
        }
    }

    private void updateDeliveryStatus(Deliver deliver, DeliveryStatus newStatus, UUID id) {
        deliver.setStatus(newStatus);
        StatusUpdate status = new StatusUpdate(deliver.getId(), newStatus.name());
        kafkaProducer.sendUpdate("delivery-status", status);
        repository.save(deliver);
    }

    private DeliveryStatus getNewStatusBasedOnPercentage(double percentageRemaining, DeliveryStatus currentStatus) {
        Map<Predicate<Double>, DeliveryStatus> statusMap = new LinkedHashMap<>(){{
            put(percentage -> percentage == AWAITING_PICKUP_PERCENTAGE, DeliveryStatus.AWAITING_PICKUP);
            put(percentage -> percentage <= AWAITING_PICKUP_PERCENTAGE && percentage > PICKED_UP_PERCENTAGE, DeliveryStatus.PICKED_UP);
            put(percentage -> percentage <= PICKED_UP_PERCENTAGE && percentage > IN_TRANSIT_PERCENTAGE, DeliveryStatus.IN_TRANSIT);
            put(percentage -> percentage <= IN_TRANSIT_PERCENTAGE && percentage > OUT_FOR_DELIVERY_PERCENTAGE, DeliveryStatus.OUT_FOR_DELIVERY);
            put(percentage -> percentage == DELIVERED_PERCENTAGE, DeliveryStatus.DELIVERED);
        }};

        return statusMap.entrySet().stream().filter(entry -> entry.getKey().test(percentageRemaining))
                .map(Map.Entry::getValue).findFirst().orElse(currentStatus);
    }
}
