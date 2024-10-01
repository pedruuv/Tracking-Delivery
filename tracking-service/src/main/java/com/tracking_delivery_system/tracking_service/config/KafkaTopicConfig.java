package com.tracking_delivery_system.tracking_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic locationUpdate(){
        return new NewTopic("location-update", 1, (short) 1);
    }
}
