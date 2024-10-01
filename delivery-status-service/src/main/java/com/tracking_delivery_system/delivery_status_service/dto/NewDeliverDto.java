package com.tracking_delivery_system.delivery_status_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record NewDeliverDto(@NotBlank String productName, @NotBlank String clientName, @Email String clientEmail, @NotBlank @Pattern(regexp = "^\\+?[0-9]{10,15}$") String clientPhone, @NotNull Long distance) {
}
