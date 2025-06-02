package com.agricultura.scheduler;

import com.agricultura.service.ReportService;
import com.agricultura.service.KeycloakTokenService;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class AutoReportScheduler {

    private static final Logger LOG = Logger.getLogger(AutoReportScheduler.class);

    @Inject
    ReportService reportService;

    @Inject
    KeycloakTokenService tokenService;

    @ConfigProperty(name = "reports.auto-generation.enabled", defaultValue = "true")
    boolean autoGenerationEnabled;

    // Reporte diario (a las 6 AM según application.properties)
    @Scheduled(cron = "{reports.auto-generation.cron}")
    public void generateDailyReport() {
        if (!autoGenerationEnabled) return;

        try {
            LOG.info("⏰ Iniciando generación automática de reporte diario");
            String token = tokenService.getAccessToken();
            reportService.generateAutomaticReport("Bearer " + token);
            LOG.info("✅ Reporte automático diario generado exitosamente");
        } catch (Exception e) {
            LOG.errorf(e, "❌ Error generando reporte automático diario");
        }
    }

    // Reporte semanal los lunes a las 7:00 AM
    @Scheduled(cron = "0 0 7 ? * MON")
    public void generateWeeklyReport() {
        if (!autoGenerationEnabled) return;

        try {
            LOG.info("⏰ Iniciando generación automática de reporte semanal");
            String token = tokenService.getAccessToken();
            reportService.generateAutomaticReport("Bearer " + token);
            LOG.info("✅ Reporte automático semanal generado exitosamente");
        } catch (Exception e) {
            LOG.errorf(e, "❌ Error generando reporte automático semanal");
        }
    }
}
