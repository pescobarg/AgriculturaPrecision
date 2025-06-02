package com.agricultura.client;

import com.agricultura.dto.SensorDataDto;
import com.agricultura.dto.SensorStatsDto;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@RegisterRestClient(configKey = "monitoring-api")
@Path("/api/monitoring")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface MonitoringClient {

    @GET
    @Path("/sensor-data")
    List<SensorDataDto> getAllSensorData(@HeaderParam("Authorization") String authorization);

    @GET
    @Path("/sensor-data/latest")
    List<SensorDataDto> getLatestSensorData(
            @QueryParam("limit") int limit,
            @HeaderParam("Authorization") String authorization);

    @GET
    @Path("/sensor-data/range")
    List<SensorDataDto> getSensorDataByRange(
            @QueryParam("from") String from,
            @QueryParam("to") String to,
            @HeaderParam("Authorization") String authorization);

    @GET
    @Path("/stats")
    SensorStatsDto getSensorStats(
            @QueryParam("from") String from,
            @QueryParam("to") String to,
            @HeaderParam("Authorization") String authorization);
}
