package com.tracking_delivery_system.delivery_status_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class DeliveryStatusServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeliveryStatusServiceApplication.class, args);
	}

}
