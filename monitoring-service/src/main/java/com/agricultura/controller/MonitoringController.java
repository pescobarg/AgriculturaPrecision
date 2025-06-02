package com.agricultura.controller;

import com.agricultura.entity.SensorData;
import com.agricultura.service.MonitoringService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Path("/api/monitoring")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class MonitoringController {

    private static final Logger LOG = Logger.getLogger(MonitoringController.class);

    @Inject
    MonitoringService monitoringService;

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/sensor-data")
    @RolesAllowed({"AGRICULTOR", "ADMINISTRADOR"})
    public Response getAllSensorData() {
        try {
            LOG.infof("🔍 Usuario '%s' con rol '%s' solicitando todos los datos del sensor", 
                jwt.getClaim("preferred_username"), 
                jwt.getClaim("realm_access"));
            
            List<SensorData> data = monitoringService.getAllSensorData();
            return Response.ok(data).build();
        } catch (Exception e) {
            LOG.errorf(e, "❌ Error obteniendo datos del sensor");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Error obteniendo datos del sensor"))
                    .build();
        }
    }

    @GET
    @Path("/sensor-data/latest")
    @RolesAllowed({"AGRICULTOR", "ADMINISTRADOR"})
    public Response getLatestSensorData(@QueryParam("limit") @DefaultValue("50") int limit) {
        try {
            LOG.infof("🔍 Usuario '%s' solicitando los últimos %d registros", 
                jwt.getClaim("preferred_username"), limit);
            
            if (limit <= 0 || limit > 1000) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "El límite debe estar entre 1 y 1000"))
                        .build();
            }
            
            List<SensorData> data = monitoringService.getLatestSensorData(limit);
            return Response.ok(data).build();
        } catch (Exception e) {
            LOG.errorf(e, "❌ Error obteniendo datos recientes del sensor");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Error obteniendo datos recientes"))
                    .build();
        }
    }

    @GET
    @Path("/sensor-data/range")
    @RolesAllowed({"AGRICULTOR", "ADMINISTRADOR"})
    public Response getSensorDataByRange(
            @QueryParam("from") String fromStr,
            @QueryParam("to") String toStr) {
        try {
            if (fromStr == null || toStr == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Los parámetros 'from' y 'to' son obligatorios"))
                        .build();
            }

            LocalDateTime from = LocalDateTime.parse(fromStr);
            LocalDateTime to = LocalDateTime.parse(toStr);

            LOG.infof("🔍 Usuario '%s' solicitando datos entre %s y %s", 
                jwt.getClaim("preferred_username"), from, to);

            List<SensorData> data = monitoringService.getSensorDataByDateRange(from, to);
            return Response.ok(data).build();
        } catch (Exception e) {
            LOG.errorf(e, "❌ Error obteniendo datos por rango de fechas");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Error en los parámetros de fecha. Formato: yyyy-MM-ddTHH:mm:ss"))
                    .build();
        }
    }

    @GET
    @Path("/stats")
    @RolesAllowed({"AGRICULTOR", "ADMINISTRADOR"})
    public Response getSensorStats(
            @QueryParam("from") String fromStr,
            @QueryParam("to") String toStr) {
        try {
            LocalDateTime from, to;
            
            if (fromStr == null || toStr == null) {
                // Si no se proporcionan fechas, usar las últimas 24 horas
                to = LocalDateTime.now();
                from = to.minusHours(24);
            } else {
                from = LocalDateTime.parse(fromStr);
                to = LocalDateTime.parse(toStr);
            }

            LOG.infof("📊 Usuario '%s' solicitando estadísticas entre %s y %s", 
                jwt.getClaim("preferred_username"), from, to);

            Map<String, Object> stats = monitoringService.getSensorStats(from, to);
            return Response.ok(stats).build();
        } catch (Exception e) {
            LOG.errorf(e, "❌ Error obteniendo estadísticas");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Error calculando estadísticas"))
                    .build();
        }
    }

    @GET
    @Path("/health")
    @RolesAllowed({"AGRICULTOR", "ADMINISTRADOR"})
    public Response getSystemHealth() {
        try {
            long totalRecords = monitoringService.getTotalRecords();
            
            Map<String, Object> health = Map.of(
                "status", "UP",
                "totalRecords", totalRecords,
                "timestamp", LocalDateTime.now(),
                "service", "monitoring-service"
            );
            
            return Response.ok(health).build();
        } catch (Exception e) {
            LOG.errorf(e, "❌ Error verificando salud del sistema");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("status", "DOWN", "error", e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/sensor-data/cleanup")
    @RolesAllowed({"ADMINISTRADOR"})
    public Response cleanupOldData(@QueryParam("days") @DefaultValue("30") int days) {
        try {
            LOG.infof("🗑️ Administrador '%s' solicitando limpieza de datos antiguos (>%d días)", 
                jwt.getClaim("preferred_username"), days);
            
            if (days < 1) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Los días deben ser mayor a 0"))
                        .build();
            }
            
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
            monitoringService.deleteOldRecords(cutoffDate);
            
            return Response.ok(Map.of(
                "message", "Limpieza completada",
                "cutoffDate", cutoffDate
            )).build();
        } catch (Exception e) {
            LOG.errorf(e, "❌ Error durante la limpieza de datos");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Error durante la limpieza"))
                    .build();
        }
    }
}