package com.agricultura.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class SensorDataDto {

    @JsonProperty("id")
    public Long id;

    @JsonProperty("temperatura")
    public BigDecimal temperatura;

    @JsonProperty("humedad") 
    public BigDecimal humedad;

    @JsonProperty("timestamp")
    public String timestamp;

    @JsonProperty("receivedAt")
    public String receivedAt;

    public SensorDataDto() {}

    @Override
    public String toString() {
        return "SensorDataDto{" +
                "id=" + id +
                ", temperatura=" + temperatura +
                ", humedad=" + humedad +
                ", timestamp='" + timestamp + '\'' +
                ", receivedAt='" + receivedAt + '\'' +
                '}';
    }
}
