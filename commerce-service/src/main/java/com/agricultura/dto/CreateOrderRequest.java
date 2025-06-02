package com.agricultura.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateOrderRequest {
    @NotBlank(message = "La dirección de envío es requerida")
    public String shippingAddress;
    
    public String paymentMethod = "TRANSFERENCIA";
}