package com.agricultura.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProductCreateDTO {
    
    @NotBlank(message = "Name is required")
    public String name;
    
    public String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    public BigDecimal price;
    
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    public Integer stock;
}