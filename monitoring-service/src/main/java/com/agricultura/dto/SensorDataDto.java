package com.agricultura.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class SensorDataDto {
    
    @JsonProperty("temperatura")
    public BigDecimal temperatura;
    
    @JsonProperty("humedad") 
    public BigDecimal humedad;
    
    @JsonProperty("timestamp")
    public String timestamp;

    public SensorDataDto() {}

    public SensorDataDto(BigDecimal temperatura, BigDecimal humedad, String timestamp) {
        this.temperatura = temperatura;
        this.humedad = humedad;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "SensorDataDto{" +
                "temperatura=" + temperatura +
                ", humedad=" + humedad +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}