package com.agricultura.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

public class ProductUpdateDTO {
    
    public String name;
    
    public String description;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    public BigDecimal price;
    
    @Min(value = 0, message = "Stock cannot be negative")
    public Integer stock;
}