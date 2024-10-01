package com.tracking_delivery_system.delivery_status_service.controller;

import com.tracking_delivery_system.delivery_status_service.dto.NewDeliverDto;
import com.tracking_delivery_system.delivery_status_service.model.Deliver;
import com.tracking_delivery_system.delivery_status_service.model.StatusUpdate;
import com.tracking_delivery_system.delivery_status_service.repository.DeliverRepository;
import com.tracking_delivery_system.delivery_status_service.service.KafkaProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/delivery")
@RequiredArgsConstructor
public class DeliverController {
    @Autowired
    private DeliverRepository repository;
    private final KafkaProducer kafkaProducer;


    @PostMapping
    public ResponseEntity<Deliver> newDelivery(@Valid @RequestBody NewDeliverDto newDeliver){
        Deliver deliver = new Deliver(newDeliver.productName(), newDeliver.clientName(), newDeliver.clientEmail(), newDeliver.clientPhone(), newDeliver.distance());

        repository.save(deliver);

        kafkaProducer.sendUpdate("new-delivery", deliver);
        StatusUpdate status = new StatusUpdate(deliver.getId(), deliver.getStatus().name());
        kafkaProducer.sendUpdate("delivery-status", status);

        return ResponseEntity.status(HttpStatus.CREATED).body(deliver);
    }
}
