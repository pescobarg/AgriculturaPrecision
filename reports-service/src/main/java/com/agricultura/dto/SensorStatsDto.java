package com.agricultura.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class SensorStatsDto {

    @JsonProperty("averageTemperature")
    public Double averageTemperature;

    @JsonProperty("averageHumidity")
    public Double averageHumidity;

    @JsonProperty("maxTemperature")
    public BigDecimal maxTemperature;

    @JsonProperty("minTemperature")
    public BigDecimal minTemperature;

    @JsonProperty("maxHumidity")
    public BigDecimal maxHumidity;

    @JsonProperty("minHumidity")
    public BigDecimal minHumidity;

    @JsonProperty("totalRecords")
    public Long totalRecords;

    @JsonProperty("fromDate")
    public String fromDate;

    @JsonProperty("toDate")
    public String toDate;

    public SensorStatsDto() {}
}