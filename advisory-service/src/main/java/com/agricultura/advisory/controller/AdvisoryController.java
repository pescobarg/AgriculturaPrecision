package com.agricultura.advisory.controller;

import com.agricultura.advisory.dto.AdvisoryDto;
import com.agricultura.advisory.entity.Advisory;
import com.agricultura.advisory.service.AdvisoryService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/api/advisory")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Advisory Service", description = "API para gestión de asesorías agrícolas")
@SecurityRequirement(name = "bearer-key")
public class AdvisoryController {

    private static final Logger LOG = Logger.getLogger(AdvisoryController.class);

    @Inject
    AdvisoryService advisoryService;

    @Inject
    JsonWebToken jwt;

    @POST
    @RolesAllowed({"AGRICULTOR"})
    @Operation(summary = "Crear nueva solicitud de asesoría", 
               description = "Permite a un agricultor crear una nueva solicitud de asesoría")
    public Response createAdvisory(@Valid AdvisoryDto.CreateAdvisoryRequest request) {
        try {
            LOG.infof("Creating advisory request from user: %s", jwt.getName());
            AdvisoryDto.AdvisoryResponse response = advisoryService.createAdvisory(request, jwt);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Error creating advisory", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno del servidor").build();
        }
    }

    @GET
    @RolesAllowed({"AGRICULTOR", "EXPERTO", "ADMINISTRADOR"})
    @Operation(summary = "Obtener mis asesorías", 
               description = "Obtiene las asesorías del usuario actual según su rol")
    public Response getMyAdvisories() {
        try {
            List<AdvisoryDto.AdvisoryResponse> advisories = advisoryService.getMyAdvisories(jwt);
            return Response.ok(advisories).build();
        } catch (Exception e) {
            LOG.error("Error getting user advisories", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno del servidor").build();
        }
    }

    @GET
    @Path("/pending")
    @RolesAllowed({"EXPERTO", "ADMINISTRADOR"})
    @Operation(summary = "Obtener asesorías pendientes", 
               description = "Obtiene todas las asesorías pendientes de respuesta")
    public Response getPendingAdvisories() {
        try {
            List<AdvisoryDto.AdvisoryResponse> advisories = advisoryService.getPendingAdvisories(jwt);
            return Response.ok(advisories).build();
        } catch (ForbiddenException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Error getting pending advisories", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno del servidor").build();
        }
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"AGRICULTOR", "EXPERTO", "ADMINISTRADOR"})
    @Operation(summary = "Obtener asesoría por ID", 
               description = "Obtiene los detalles de una asesoría específica")
    public Response getAdvisoryById(@Parameter(description = "ID de la asesoría") @PathParam("id") Long id) {
        try {
            AdvisoryDto.AdvisoryResponse advisory = advisoryService.getAdvisoryById(id, jwt);
            return Response.ok(advisory).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("Asesoría no encontrada").build();
        } catch (ForbiddenException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Error getting advisory by ID", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno del servidor").build();
        }
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"AGRICULTOR"})
    @Operation(summary = "Actualizar solicitud de asesoría", 
               description = "Permite al agricultor actualizar su solicitud de asesoría")
    public Response updateAdvisory(@Parameter(description = "ID de la asesoría") @PathParam("id") Long id,
                                   @Valid AdvisoryDto.UpdateAdvisoryRequest request) {
        try {
            AdvisoryDto.AdvisoryResponse response = advisoryService.updateAdvisory(id, request, jwt);
            return Response.ok(response).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("Asesoría no encontrada").build();
        } catch (ForbiddenException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Error updating advisory", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno del servidor").build();
        }
    }

    @POST
    @Path("/{id}/respond")
    @RolesAllowed({"EXPERTO"})
    @Operation(summary = "Responder a solicitud de asesoría", 
               description = "Permite al experto responder a una solicitud de asesoría")
    public Response respondToAdvisory(@Parameter(description = "ID de la asesoría") @PathParam("id") Long id,
                                      @Valid AdvisoryDto.ExpertResponseRequest request) {
        try {
            AdvisoryDto.AdvisoryResponse response = advisoryService.respondToAdvisory(id, request, jwt);
            return Response.ok(response).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("Asesoría no encontrada").build();
        } catch (ForbiddenException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Error responding to advisory", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno del servidor").build();
        }
    }

    @PATCH
    @Path("/{id}/status")
    @RolesAllowed({"AGRICULTOR", "EXPERTO", "ADMINISTRADOR"})
    @Operation(summary = "Actualizar estado de asesoría", 
               description = "Actualiza el estado de una asesoría según el rol del usuario")
    public Response updateAdvisoryStatus(@Parameter(description = "ID de la asesoría") @PathParam("id") Long id,
                                         @QueryParam("status") Advisory.EstadoAsesoria newStatus) {
        try {
            if (newStatus == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("El parámetro 'status' es requerido").build();
            }

            AdvisoryDto.AdvisoryResponse response = advisoryService.updateAdvisoryStatus(id, newStatus, jwt);
            return Response.ok(response).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("Asesoría no encontrada").build();
        } catch (ForbiddenException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Error updating advisory status", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno del servidor").build();
        }
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"AGRICULTOR", "ADMINISTRADOR"})
    @Operation(summary = "Eliminar asesoría", 
               description = "Elimina una asesoría (solo si está pendiente o rechazada)")
    public Response deleteAdvisory(@Parameter(description = "ID de la asesoría") @PathParam("id") Long id) {
        try {
            advisoryService.deleteAdvisory(id, jwt);
            return Response.ok("Asesoría eliminada exitosamente").build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("Asesoría no encontrada").build();
        } catch (ForbiddenException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Error deleting advisory", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno del servidor").build();
        }
    }

    @GET
    @Path("/statistics")
    @RolesAllowed({"ADMINISTRADOR"})
    @Operation(summary = "Obtener estadísticas de asesorías", 
               description = "Obtiene estadísticas generales de las asesorías")
    public Response getAdvisoriesStatistics() {
        try {
            var stats = new java.util.HashMap<String, Object>();
            
            // Contar por estado
            for (Advisory.EstadoAsesoria estado : Advisory.EstadoAsesoria.values()) {
                long count = Advisory.count("estado", estado);
                stats.put("total_" + estado.name().toLowerCase(), count);
            }
            
            // Total general
            stats.put("total_general", Advisory.count());
            
            // Contar por tipo de asesoría
            var tipos = Advisory.find("SELECT DISTINCT tipoAsesoria FROM Advisory").list();
            for (Object tipo : tipos) {
                long count = Advisory.count("tipoAsesoria", tipo);
                stats.put("tipo_" + tipo.toString().toLowerCase(), count);
            }

            return Response.ok(stats).build();
        } catch (ForbiddenException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Error getting statistics", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno del servidor").build();
        }
    }

    @GET
    @Path("/health")
    @Operation(summary = "Health check", description = "Verificar el estado del servicio")
    public Response healthCheck() {
        return Response.ok(java.time.LocalDateTime.now()).build();
    }
}