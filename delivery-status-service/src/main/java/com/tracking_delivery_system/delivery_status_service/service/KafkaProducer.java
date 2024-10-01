package com.tracking_delivery_system.delivery_status_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public <T> void sendUpdate(String topic, T data){
        kafkaTemplate.send(topic, data);
    }
}
