package com.agricultura.dto;

import com.agricultura.entity.Order.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateOrderStatusRequest {
    @NotNull(message = "El estado es requerido")
    public OrderStatus status;
}
