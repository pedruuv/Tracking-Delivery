package com.tracking_delivery_system.delivery_status_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//review
@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic newDelivery(){
        return new NewTopic("new-delivery", 1, (short) 1);
    }
}
