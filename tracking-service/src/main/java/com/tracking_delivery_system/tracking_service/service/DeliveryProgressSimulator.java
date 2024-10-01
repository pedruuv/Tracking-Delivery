package com.tracking_delivery_system.tracking_service.service;

import com.tracking_delivery_system.tracking_service.model.LocationUpdate;
import com.tracking_delivery_system.tracking_service.repository.LocationUpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class DeliveryProgressSimulator {
    private final KafkaProducer producer;
    private final LocationUpdateRepository repository;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ConcurrentMap<UUID, ScheduledFuture<?>> tasksMap = new ConcurrentHashMap<>();

    public void simulateDeliverProgress(LocationUpdate locationUpdate) {
        final long[] distance = {locationUpdate.getDistance()};
        final UUID deliveryId = locationUpdate.getId();
        ScheduledFuture<?> existingTask = tasksMap.get(deliveryId);

        if (existingTask != null && !existingTask.isDone()) {
            existingTask.cancel(false);
        }

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
            if (distance[0] > 0) {
                producer.sendUpdate("location-update", locationUpdate);
                distance[0] -= calculateDistanceReduction();

                locationUpdate.setDistance(distance[0]);
                repository.save(locationUpdate);
            } else {
                locationUpdate.setDistance(0L);
                producer.sendUpdate("location-update", locationUpdate);
                repository.save(locationUpdate);
                assert existingTask != null;
                existingTask.cancel(true);
                tasksMap.remove(deliveryId);
            }
        }, 0, 10, TimeUnit.SECONDS);

        tasksMap.put(deliveryId, future);
    }

    private Long calculateDistanceReduction() {
        Random random = new Random();
        double trafficCondition = getTrafficCondition();
        long baseReduction = 10;
        return Math.round(baseReduction * trafficCondition * random.nextDouble());
    }

    private double getTrafficCondition() {
        Random random = new Random();
        return 0.5 + (random.nextDouble() * 1.5);
    }
}
