package com.agricultura.dto;

import com.agricultura.entity.Report;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class ReportSummaryDto {
    
    @JsonProperty("id")
    public Long id;
    
    @JsonProperty("title")
    public String title;
    
    @JsonProperty("analysisType")
    public Report.AnalysisType analysisType;
    
    @JsonProperty("status")
    public Report.ReportStatus status;
    
    @JsonProperty("createdAt")
    public LocalDateTime createdAt;
    
    @JsonProperty("createdByUsername")
    public String createdByUsername;
    
    @JsonProperty("totalRecordsAnalyzed")
    public Long totalRecordsAnalyzed;
    
    @JsonProperty("anomaliesDetected")
    public Integer anomaliesDetected;
    
    @JsonProperty("isAutomatic")
    public Boolean isAutomatic;

    public ReportSummaryDto() {}

    public static ReportSummaryDto fromReport(Report report) {
        ReportSummaryDto dto = new ReportSummaryDto();
        dto.id = report.id;
        dto.title = report.title;
        dto.analysisType = report.analysisType;
        dto.status = report.status;
        dto.createdAt = report.createdAt;
        dto.createdByUsername = report.createdByUsername;
        dto.totalRecordsAnalyzed = report.totalRecordsAnalyzed;
        dto.anomaliesDetected = report.anomaliesDetected;
        dto.isAutomatic = report.isAutomatic;
        return dto;
    }
}
