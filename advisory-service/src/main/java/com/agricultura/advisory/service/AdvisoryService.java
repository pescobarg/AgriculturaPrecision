package com.agricultura.advisory.service;

import com.agricultura.advisory.dto.AdvisoryDto;
import com.agricultura.advisory.entity.Advisory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AdvisoryService {

    private static final Logger LOG = Logger.getLogger(AdvisoryService.class);

    @Transactional
    public AdvisoryDto.AdvisoryResponse createAdvisory(AdvisoryDto.CreateAdvisoryRequest request, JsonWebToken jwt) {
        LOG.infof("Creating advisory for user: %s", jwt.getName());

        // Validar que el usuario sea AGRICULTOR
        if (!hasRole(jwt, "AGRICULTOR")) {
            throw new ForbiddenException("Solo los agricultores pueden crear solicitudes de asesoría");
        }

        // Validar modalidad presencial requiere ubicación
        if (request.modalidad == Advisory.ModalidadAsesoria.PRESENCIAL && 
            (request.ubicacion == null || request.ubicacion.trim().isEmpty())) {
            throw new BadRequestException("La modalidad presencial requiere especificar una ubicación");
        }

        Advisory advisory = new Advisory();
        advisory.agricultorUsername = jwt.getName();
        advisory.agricultorEmail = jwt.getClaim("email");
        advisory.agricultorName = getFullName(jwt);
        advisory.titulo = request.titulo;
        advisory.descripcion = request.descripcion;
        advisory.tipoAsesoria = request.tipoAsesoria.toUpperCase();
        advisory.fechaSolicitada = request.fechaSolicitada;
        advisory.duracionEstimada = request.duracionEstimada;
        advisory.modalidad = request.modalidad;
        advisory.ubicacion = request.ubicacion;
        advisory.notasAgricultor = request.notasAgricultor;
        advisory.expertoUsername = request.expertoUsername;
        advisory.estado = Advisory.EstadoAsesoria.PENDIENTE;

        advisory.persist();
        LOG.infof("Advisory created with ID: %d", advisory.id);

        return AdvisoryDto.AdvisoryResponse.fromEntity(advisory);
    }

    @Transactional
    public AdvisoryDto.AdvisoryResponse updateAdvisory(Long id, AdvisoryDto.UpdateAdvisoryRequest request, JsonWebToken jwt) {
        Advisory advisory = Advisory.findById(id);
        if (advisory == null) {
            throw new NotFoundException("Asesoría no encontrada");
        }

        // Solo el agricultor dueño puede modificar la solicitud
        if (!advisory.agricultorUsername.equals(jwt.getName())) {
            throw new ForbiddenException("No tienes permisos para modificar esta asesoría");
        }

        // Solo se puede modificar si está pendiente
        if (advisory.estado != Advisory.EstadoAsesoria.PENDIENTE) {
            throw new BadRequestException("Solo se pueden modificar asesorías pendientes");
        }

        if (request.titulo != null) advisory.titulo = request.titulo;
        if (request.descripcion != null) advisory.descripcion = request.descripcion;
        if (request.tipoAsesoria != null) advisory.tipoAsesoria = request.tipoAsesoria.toUpperCase();
        if (request.fechaSolicitada != null) advisory.fechaSolicitada = request.fechaSolicitada;
        if (request.duracionEstimada != null) advisory.duracionEstimada = request.duracionEstimada;
        if (request.modalidad != null) advisory.modalidad = request.modalidad;
        if (request.ubicacion != null) advisory.ubicacion = request.ubicacion;
        if (request.notasAgricultor != null) advisory.notasAgricultor = request.notasAgricultor;

        advisory.fechaActualizacion = LocalDateTime.now();

        return AdvisoryDto.AdvisoryResponse.fromEntity(advisory);
    }

    @Transactional
    public AdvisoryDto.AdvisoryResponse respondToAdvisory(Long id, AdvisoryDto.ExpertResponseRequest request, JsonWebToken jwt) {
        Advisory advisory = Advisory.findById(id);
        if (advisory == null) {
            throw new NotFoundException("Asesoría no encontrada");
        }

        // Solo los expertos pueden responder
        if (!hasRole(jwt, "EXPERTO")) {
            throw new ForbiddenException("Solo los expertos pueden responder a las solicitudes");
        }

        // Solo se puede responder a asesorías pendientes
        if (advisory.estado != Advisory.EstadoAsesoria.PENDIENTE) {
            throw new BadRequestException("Esta asesoría ya no está pendiente");
        }

        // Si se acepta, asignar el experto
        if (request.estado == Advisory.EstadoAsesoria.ACEPTADA || 
            request.estado == Advisory.EstadoAsesoria.PROGRAMADA) {
            advisory.expertoUsername = jwt.getName();
            advisory.expertoEmail = jwt.getClaim("email");
            advisory.expertoName = getFullName(jwt);
            advisory.fechaPropuesta = request.fechaPropuesta;
            advisory.linkReunion = request.linkReunion;
            if (request.ubicacion != null) advisory.ubicacion = request.ubicacion;
        }

        advisory.estado = request.estado;
        advisory.notasExperto = request.notasExperto;
        advisory.fechaActualizacion = LocalDateTime.now();

        LOG.infof("Advisory %d updated by expert %s with status %s", id, jwt.getName(), request.estado);

        return AdvisoryDto.AdvisoryResponse.fromEntity(advisory);
    }

    public List<AdvisoryDto.AdvisoryResponse> getMyAdvisories(JsonWebToken jwt) {
        String username = jwt.getName();
        List<Advisory> advisories;

        if (hasRole(jwt, "AGRICULTOR")) {
            advisories = Advisory.list("agricultorUsername", username);
        } else if (hasRole(jwt, "EXPERTO")) {
            advisories = Advisory.list("expertoUsername = ?1 OR (expertoUsername IS NULL AND estado = ?2)", 
                                     username, Advisory.EstadoAsesoria.PENDIENTE);
        } else if (hasRole(jwt, "ADMINISTRADOR")) {
            advisories = Advisory.listAll();
        } else {
            advisories = List.of();
        }

        return advisories.stream()
                .map(AdvisoryDto.AdvisoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public AdvisoryDto.AdvisoryResponse getAdvisoryById(Long id, JsonWebToken jwt) {
        Advisory advisory = Advisory.findById(id);
        if (advisory == null) {
            throw new NotFoundException("Asesoría no encontrada");
        }

        // Verificar permisos
        if (!hasPermissionToView(advisory, jwt)) {
            throw new ForbiddenException("No tienes permisos para ver esta asesoría");
        }

        return AdvisoryDto.AdvisoryResponse.fromEntity(advisory);
    }

    public List<AdvisoryDto.AdvisoryResponse> getPendingAdvisories(JsonWebToken jwt) {
        if (!hasRole(jwt, "EXPERTO") && !hasRole(jwt, "ADMINISTRADOR")) {
            throw new ForbiddenException("Solo expertos y administradores pueden ver asesorías pendientes");
        }

        List<Advisory> advisories = Advisory.list("estado", Advisory.EstadoAsesoria.PENDIENTE);
        return advisories.stream()
                .map(AdvisoryDto.AdvisoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public AdvisoryDto.AdvisoryResponse updateAdvisoryStatus(Long id, Advisory.EstadoAsesoria newStatus, JsonWebToken jwt) {
        Advisory advisory = Advisory.findById(id);
        if (advisory == null) {
            throw new NotFoundException("Asesoría no encontrada");
        }

        // Verificar permisos según el estado
        if (!canUpdateStatus(advisory, newStatus, jwt)) {
            throw new ForbiddenException("No tienes permisos para cambiar a este estado");
        }

        advisory.estado = newStatus;
        advisory.fechaActualizacion = LocalDateTime.now();

        LOG.infof("Advisory %d status updated to %s by %s", id, newStatus, jwt.getName());

        return AdvisoryDto.AdvisoryResponse.fromEntity(advisory);
    }

    @Transactional
    public void deleteAdvisory(Long id, JsonWebToken jwt) {
        Advisory advisory = Advisory.findById(id);
        if (advisory == null) {
            throw new NotFoundException("Asesoría no encontrada");
        }

        // Solo el agricultor dueño o un administrador puede eliminar
        if (!advisory.agricultorUsername.equals(jwt.getName()) && !hasRole(jwt, "ADMINISTRADOR")) {
            throw new ForbiddenException("No tienes permisos para eliminar esta asesoría");
        }

        // Solo se puede eliminar si está pendiente o rechazada
        if (advisory.estado != Advisory.EstadoAsesoria.PENDIENTE && 
            advisory.estado != Advisory.EstadoAsesoria.RECHAZADA) {
            throw new BadRequestException("Solo se pueden eliminar asesorías pendientes o rechazadas");
        }

        advisory.delete();
        LOG.infof("Advisory %d deleted by %s", id, jwt.getName());
    }

    // Métodos auxiliares
    private boolean hasRole(JsonWebToken jwt, String role) {
        return jwt.getGroups() != null && jwt.getGroups().contains(role);
    }

    private String getFullName(JsonWebToken jwt) {
        String firstName = jwt.getClaim("given_name");
        String lastName = jwt.getClaim("family_name");
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return jwt.getName();
    }

    private boolean hasPermissionToView(Advisory advisory, JsonWebToken jwt) {
        String username = jwt.getName();
        return advisory.agricultorUsername.equals(username) ||
               (advisory.expertoUsername != null && advisory.expertoUsername.equals(username)) ||
               hasRole(jwt, "ADMINISTRADOR");
    }

    private boolean canUpdateStatus(Advisory advisory, Advisory.EstadoAsesoria newStatus, JsonWebToken jwt) {
        String username = jwt.getName();
        
        // Administradores pueden cambiar cualquier estado
        if (hasRole(jwt, "ADMINISTRADOR")) {
            return true;
        }
        
        // Agricultores solo pueden cancelar sus propias asesorías
        if (hasRole(jwt, "AGRICULTOR") && advisory.agricultorUsername.equals(username)) {
            return newStatus == Advisory.EstadoAsesoria.CANCELADA;
        }
        
        // Expertos pueden cambiar estados según las reglas de negocio
        if (hasRole(jwt, "EXPERTO") && 
            (advisory.expertoUsername == null || advisory.expertoUsername.equals(username))) {
            return isValidStatusTransition(advisory.estado, newStatus);
        }
        
        return false;
    }

    private boolean isValidStatusTransition(Advisory.EstadoAsesoria currentStatus, Advisory.EstadoAsesoria newStatus) {
        return switch (currentStatus) {
            case PENDIENTE -> newStatus == Advisory.EstadoAsesoria.ACEPTADA || 
                           newStatus == Advisory.EstadoAsesoria.RECHAZADA;
            case ACEPTADA -> newStatus == Advisory.EstadoAsesoria.PROGRAMADA;
            case PROGRAMADA -> newStatus == Advisory.EstadoAsesoria.EN_CURSO || 
                             newStatus == Advisory.EstadoAsesoria.CANCELADA;
            case EN_CURSO -> newStatus == Advisory.EstadoAsesoria.COMPLETADA;
            default -> false;
        };
    }
}