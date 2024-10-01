package com.tracking_delivery_system.tracking_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "location-update")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationUpdate {
    @Id
    private UUID id;
    private Long distance;
}
