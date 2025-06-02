package com.agricultura.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "reports")
public class Report extends PanacheEntity {

    @Column(nullable = false)
    public String title;

    @Column(columnDefinition = "TEXT")
    public String content;

    @Column(name = "analysis_type", nullable = false)
    @Enumerated(EnumType.STRING)
    public AnalysisType analysisType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public ReportStatus status;

    @Column(name = "created_by_user_id", nullable = false)
    public Integer createdByUserId;

    @Column(name = "created_by_username", nullable = false)
    public String createdByUsername;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;

    @Column(name = "data_from")
    public LocalDateTime dataFrom;

    @Column(name = "data_to")
    public LocalDateTime dataTo;

    @Column(name = "total_records_analyzed")
    public Long totalRecordsAnalyzed;

    @Column(columnDefinition = "TEXT")
    public String recommendations;

    @Column(name = "anomalies_detected")
    public Integer anomaliesDetected;

    @Column(name = "is_automatic")
    public Boolean isAutomatic = false;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = ReportStatus.PENDING;
        }
    }

    // Métodos de consulta
    public static List<Report> findByUser(Integer userId) {
        return find("createdByUserId = ?1 order by createdAt desc", userId).list();
    }

    public static List<Report> findByDateRange(LocalDateTime from, LocalDateTime to) {
        return find("createdAt >= ?1 and createdAt <= ?2 order by createdAt desc", from, to).list();
    }

    public static List<Report> findByStatus(ReportStatus status) {
        return find("status = ?1 order by createdAt desc", status).list();
    }

    public static List<Report> findByAnalysisType(AnalysisType analysisType) {
        return find("analysisType = ?1 order by createdAt desc", analysisType).list();
    }

    public static List<Report> findAutomaticReports() {
        return find("isAutomatic = true order by createdAt desc").list();
    }

    public enum AnalysisType {
        TEMPERATURE_ANALYSIS,
        HUMIDITY_ANALYSIS,
        GENERAL_MONITORING,
        ANOMALY_DETECTION,
        PREDICTIVE_ANALYSIS
    }

    public enum ReportStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }
}
