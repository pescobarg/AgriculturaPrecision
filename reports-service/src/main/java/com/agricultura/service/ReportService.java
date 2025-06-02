package com.agricultura.service;

import com.agricultura.client.MonitoringClient;
import com.agricultura.dto.SensorDataDto;
import com.agricultura.dto.SensorStatsDto;
import com.agricultura.entity.Report;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ApplicationScoped
public class ReportService {

    private static final Logger LOG = Logger.getLogger(ReportService.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Inject
    @RestClient
    MonitoringClient monitoringClient;

    @Inject
    AIReportService aiReportService;

    @Transactional
    public Report generateReport(Report.AnalysisType analysisType, Integer userId, String username, 
                               LocalDateTime from, LocalDateTime to, String authToken) {
        LOG.infof("📊 Generando reporte de tipo %s para usuario %s", analysisType, username);

        Report report = new Report();
        report.analysisType = analysisType;
        report.createdByUserId = userId;
        report.createdByUsername = username;
        report.dataFrom = from;
        report.dataTo = to;
        report.status = Report.ReportStatus.PROCESSING;
        report.isAutomatic = false;

        try {
            // Obtener datos del monitoring-service
            String fromStr = from.format(FORMATTER);
            String toStr = to.format(FORMATTER);
            
            List<SensorDataDto> sensorData = monitoringClient.getSensorDataByRange(fromStr, toStr, "Bearer " + authToken);
            SensorStatsDto stats = monitoringClient.getSensorStats(fromStr, toStr, "Bearer " + authToken);

            report.totalRecordsAnalyzed = stats.totalRecords;

            // Generar análisis con IA según el tipo
            switch (analysisType) {
                case TEMPERATURE_ANALYSIS:
                    report.title = "Análisis de Temperatura - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                    report.content = aiReportService.generateTemperatureAnalysis(sensorData, stats);
                    break;
                case HUMIDITY_ANALYSIS:
                    report.title = "Análisis de Humedad - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                    report.content = aiReportService.generateHumidityAnalysis(sensorData, stats);
                    break;
                case GENERAL_MONITORING:
                    report.title = "Análisis General de Cultivo - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                    report.content = aiReportService.generateGeneralAnalysis(sensorData, stats);
                    break;
                case ANOMALY_DETECTION:
                    report.title = "Detección de Anomalías - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                    report.content = aiReportService.generateGeneralAnalysis(sensorData, stats);
                    report.anomaliesDetected = aiReportService.detectAnomalies(sensorData, stats);
                    break;
                case PREDICTIVE_ANALYSIS:
                    report.title = "Análisis Predictivo - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                    report.content = aiReportService.generateGeneralAnalysis(sensorData, stats);
                    break;
            }

            // Generar recomendaciones
            report.recommendations = aiReportService.generateRecommendations(sensorData, stats);
            report.status = Report.ReportStatus.COMPLETED;

            LOG.infof("✅ Reporte generado exitosamente con %d registros analizados", report.totalRecordsAnalyzed);

        } catch (Exception e) {
            LOG.errorf(e, "❌ Error generando reporte");
            report.status = Report.ReportStatus.FAILED;
            report.content = "Error generando el reporte: " + e.getMessage();
        }

        report.persist();
        return report;
    }

    @Transactional
    public Report generateAutomaticReport(String authToken) {
        LOG.info("🤖 Generando reporte automático diario");

        Report report = new Report();
        report.analysisType = Report.AnalysisType.GENERAL_MONITORING;
        report.createdByUserId = 0; // Sistema
        report.createdByUsername = "SISTEMA";
        report.isAutomatic = true;
        report.status = Report.ReportStatus.PROCESSING;

        // Análisis de las últimas 24 horas
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = to.minusHours(24);
        report.dataFrom = from;
        report.dataTo = to;

        try {
            String fromStr = from.format(FORMATTER);
            String toStr = to.format(FORMATTER);
            
            List<SensorDataDto> sensorData = monitoringClient.getSensorDataByRange(fromStr, toStr, "Bearer " + authToken);
            SensorStatsDto stats = monitoringClient.getSensorStats(fromStr, toStr, "Bearer " + authToken);

            report.totalRecordsAnalyzed = stats.totalRecords;
            report.title = "Reporte Automático Diario - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            report.content = aiReportService.generateGeneralAnalysis(sensorData, stats);
            report.recommendations = aiReportService.generateRecommendations(sensorData, stats);
            report.anomaliesDetected = aiReportService.detectAnomalies(sensorData, stats);
            report.status = Report.ReportStatus.COMPLETED;

            LOG.infof("✅ Reporte automático generado con %d registros analizados", report.totalRecordsAnalyzed);

        } catch (Exception e) {
            LOG.errorf(e, "❌ Error generando reporte automático");
            report.status = Report.ReportStatus.FAILED;
            report.content = "Error generando el reporte automático: " + e.getMessage();
        }

        report.persist();
        return report;
    }

    public List<Report> getReportsByUser(Integer userId) {
        return Report.findByUser(userId);
    }

    public List<Report> getAllReports() {
        return Report.listAll();
    }

    public Report getReportById(Long id) {
        return Report.findById(id);
    }

    @Transactional
    public void deleteReport(Long id) {
        Report report = Report.findById(id);
        if (report != null) {
            report.delete();
            LOG.infof("🗑️ Reporte eliminado: ID=%d", id);
        }
    }

    public List<Report> getReportsByDateRange(LocalDateTime from, LocalDateTime to) {
        return Report.findByDateRange(from, to);
    }

    public List<Report> getReportsByStatus(Report.ReportStatus status) {
        return Report.findByStatus(status);
    }

    public List<Report> getAutomaticReports() {
        return Report.findAutomaticReports();
    }
}
