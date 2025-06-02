package com.agricultura.controller;

import com.agricultura.entity.Report;
import com.agricultura.service.ReportService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Path("/api/reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class ReportController {

    private static final Logger LOG = Logger.getLogger(ReportController.class);

    @Inject
    ReportService reportService;

    @Inject
    JsonWebToken jwt;

    @POST
    @Path("/generate")
    @RolesAllowed({"AGRICULTOR", "ADMINISTRADOR"})
    public Response generateReport(
            @QueryParam("type") String analysisType,
            @QueryParam("from") String fromStr,
            @QueryParam("to") String toStr) {
        try {
            if (analysisType == null || fromStr == null || toStr == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Los parámetros 'type', 'from' y 'to' son obligatorios"))
                        .build();
            }

            Report.AnalysisType type = Report.AnalysisType.valueOf(analysisType.toUpperCase());
            LocalDateTime from = LocalDateTime.parse(fromStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime to = LocalDateTime.parse(toStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            Integer userId = jwt.getClaim("sub") != null ? 
                Integer.valueOf(jwt.getClaim("sub").toString()) : 0;
            String username = jwt.getClaim("preferred_username");

            LOG.infof("📊 Usuario '%s' generando reporte de tipo %s", username, type);

            // Obtener el token de autorización del header
            String authHeader = jwt.getRawToken();

            Report report = reportService.generateReport(type, userId, username, from, to, authHeader);
            
            return Response.ok(report).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Tipo de análisis inválido. Tipos válidos: TEMPERATURE_ANALYSIS, HUMIDITY_ANALYSIS, GENERAL_MONITORING, ANOMALY_DETECTION, PREDICTIVE_ANALYSIS"))
                    .build();
        } catch (Exception e) {
            LOG.errorf(e, "❌ Error generando reporte");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Error generando reporte: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/my-reports")
    @RolesAllowed({"AGRICULTOR", "ADMINISTRADOR"})
    public Response getMyReports() {
        try {
            Integer userId = jwt.getClaim("sub") != null ? 
                Integer.valueOf(jwt.getClaim("sub").toString()) : 0;
            String username = jwt.getClaim("preferred_username");

            LOG.infof("📋 Usuario '%s' consultando sus reportes", username);

            List<Report> reports = reportService.getReportsByUser(userId);
            return Response.ok(reports).build();
        } catch (Exception e) {
            LOG.errorf(e, "❌ Error obteniendo reportes del usuario");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Error obteniendo reportes"))
                    .build();
        }
    }

    @GET
    @Path("/all")
    @RolesAllowed({"ADMINISTRADOR"})
    public Response getAllReports() {
        try {
            LOG.info("📋 Administrador consultando todos los reportes");
            List<Report> reports = reportService.getAllReports();
            return Response.ok(reports).build();
        } catch (Exception e) {
            LOG.errorf(e, "❌ Error obteniendo todos los reportes");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Error obteniendo reportes"))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"AGRICULTOR", "ADMINISTRADOR"})
    public Response getReportById(@PathParam("id") Long id) {
        try {
            Report report = reportService.getReportById(id);
            if (report == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Reporte no encontrado"))
                        .build();
            }

            // Verificar que el usuario puede ver este reporte
            Integer userId = jwt.getClaim("sub") != null ? 
                Integer.valueOf(jwt.getClaim("sub").toString()) : 0;
            String userRole = jwt.getClaim("realm_access") != null ? 
                jwt.getClaim("realm_access").toString() : "";

            if (!userRole.contains("ADMINISTRADOR") && !report.createdByUserId.equals(userId)) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(Map.of("error", "No autorizado para ver este reporte"))
                        .build();
            }

            return Response.ok(report).build();
        } catch (Exception e) {
            LOG.errorf(e, "❌ Error obteniendo reporte por ID");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Error obteniendo reporte"))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"AGRICULTOR", "ADMINISTRADOR"})
    public Response deleteReport(@PathParam("id") Long id) {
        try {
            Report report = reportService.getReportById(id);
            if (report == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Reporte no encontrado"))
                        .build();
            }

            // Verificar que el usuario puede eliminar este reporte
            Integer userId = jwt.getClaim("sub") != null ? 
                Integer.valueOf(jwt.getClaim("sub").toString()) : 0;
            String userRole = jwt.getClaim("realm_access") != null ? 
                jwt.getClaim("realm_access").toString() : "";

            if (!userRole.contains("ADMINISTRADOR") && !report.createdByUserId.equals(userId)) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(Map.of("error", "No autorizado para eliminar este reporte"))
                        .build();
            }

            reportService.deleteReport(id);
            return Response.ok(Map.of("message", "Reporte eliminado correctamente")).build();
        } catch (Exception e) {
            LOG.errorf(e, "❌ Error eliminando reporte");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Error eliminando reporte"))
                    .build();
        }
    }

    @GET
    @Path("/automatic")
    @RolesAllowed({"ADMINISTRADOR"})
    public Response getAutomaticReports() {
        try {
            LOG.info("🤖 Consultando reportes automáticos");
            List<Report> reports = reportService.getAutomaticReports();
            return Response.ok(reports).build();
        } catch (Exception e) {
            LOG.errorf(e, "❌ Error obteniendo reportes automáticos");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Error obteniendo reportes automáticos"))
                    .build();
        }
    }

    @GET
    @Path("/stats")
    @RolesAllowed({"ADMINISTRADOR"})
    public Response getReportsStats() {
        try {
            LOG.info("📊 Consultando estadísticas de reportes");
            
            List<Report> allReports = reportService.getAllReports();
            long totalReports = allReports.size();
            long completedReports = allReports.stream()
                .filter(r -> r.status == Report.ReportStatus.COMPLETED)
                .count();
            long failedReports = allReports.stream()
                .filter(r -> r.status == Report.ReportStatus.FAILED)
                .count();
            long automaticReports = allReports.stream()
                .filter(r -> r.isAutomatic != null && r.isAutomatic)
                .count();

            Map<String, Object> stats = Map.of(
                "totalReports", totalReports,
                "completedReports", completedReports,
                "failedReports", failedReports,
                "automaticReports", automaticReports,
                "successRate", totalReports > 0 ? (completedReports * 100.0 / totalReports) : 0.0
            );

            return Response.ok(stats).build();
        } catch (Exception e) {
            LOG.errorf(e, "❌ Error obteniendo estadísticas de reportes");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Error obteniendo estadísticas"))
                    .build();
        }
    }

    @GET
    @Path("/types")
    @RolesAllowed({"AGRICULTOR", "ADMINISTRADOR"})
    public Response getAnalysisTypes() {
        return Response.ok(Map.of(
            "analysisTypes", new String[]{
                "TEMPERATURE_ANALYSIS",
                "HUMIDITY_ANALYSIS", 
                "GENERAL_MONITORING",
                "ANOMALY_DETECTION",
                "PREDICTIVE_ANALYSIS"
            },
            "descriptions", Map.of(
                "TEMPERATURE_ANALYSIS", "Análisis detallado de patrones de temperatura",
                "HUMIDITY_ANALYSIS", "Análisis detallado de patrones de humedad",
                "GENERAL_MONITORING", "Análisis general de todas las condiciones ambientales",
                "ANOMALY_DETECTION", "Detección de anomalías y valores atípicos",
                "PREDICTIVE_ANALYSIS", "Análisis predictivo basado en tendencias históricas"
            )
        )).build();
    }
}
