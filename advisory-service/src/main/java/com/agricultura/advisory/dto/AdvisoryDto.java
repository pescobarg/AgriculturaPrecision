package com.agricultura.advisory.dto;

import com.agricultura.advisory.entity.Advisory;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class AdvisoryDto {

    public static class CreateAdvisoryRequest {
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 200, message = "El título no debe exceder 200 caracteres")
        public String titulo;

        @Size(max = 1000, message = "La descripción no debe exceder 1000 caracteres")
        public String descripcion;

        @NotBlank(message = "El tipo de asesoría es obligatorio")
        public String tipoAsesoria;

        @NotNull(message = "La fecha solicitada es obligatoria")
        @Future(message = "La fecha solicitada debe ser futura")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        public LocalDateTime fechaSolicitada;

        @Min(value = 15, message = "La duración mínima es 15 minutos")
        @Max(value = 480, message = "La duración máxima es 8 horas")
        public Integer duracionEstimada = 60; 

        @NotNull(message = "La modalidad es obligatoria")
        public Advisory.ModalidadAsesoria modalidad;

        public String ubicacion; // Requerido si modalidad es PRESENCIAL

        @Size(max = 500, message = "Las notas no deben exceder 500 caracteres")
        public String notasAgricultor;

        public String expertoUsername; // Opcional, para solicitar un experto específico
    }

    public static class UpdateAdvisoryRequest {
        public String titulo;
        public String descripcion;
        public String tipoAsesoria;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        public LocalDateTime fechaSolicitada;
        
        public Integer duracionEstimada;
        public Advisory.ModalidadAsesoria modalidad;
        public String ubicacion;
        public String notasAgricultor;
    }

    public static class ExpertResponseRequest {
        @NotNull(message = "El estado es obligatorio")
        public Advisory.EstadoAsesoria estado;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        public LocalDateTime fechaPropuesta;

        public String linkReunion;
        public String ubicacion;

        @Size(max = 500, message = "Las notas no deben exceder 500 caracteres")
        public String notasExperto;
    }

    public static class AdvisoryResponse {
        public Long id;
        public String agricultorUsername;
        public String agricultorEmail;
        public String agricultorName;
        public String expertoUsername;
        public String expertoEmail;
        public String expertoName;
        public String titulo;
        public String descripcion;
        public String tipoAsesoria;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        public LocalDateTime fechaSolicitada;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        public LocalDateTime fechaPropuesta;
        
        public Integer duracionEstimada;
        public Advisory.EstadoAsesoria estado;
        public Advisory.ModalidadAsesoria modalidad;
        public String ubicacion;
        public String linkReunion;
        public String notasAgricultor;
        public String notasExperto;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        public LocalDateTime fechaCreacion;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        public LocalDateTime fechaActualizacion;

        public static AdvisoryResponse fromEntity(Advisory advisory) {
            AdvisoryResponse response = new AdvisoryResponse();
            response.id = advisory.id;
            response.agricultorUsername = advisory.agricultorUsername;
            response.agricultorEmail = advisory.agricultorEmail;
            response.agricultorName = advisory.agricultorName;
            response.expertoUsername = advisory.expertoUsername;
            response.expertoEmail = advisory.expertoEmail;
            response.expertoName = advisory.expertoName;
            response.titulo = advisory.titulo;
            response.descripcion = advisory.descripcion;
            response.tipoAsesoria = advisory.tipoAsesoria;
            response.fechaSolicitada = advisory.fechaSolicitada;
            response.fechaPropuesta = advisory.fechaPropuesta;
            response.duracionEstimada = advisory.duracionEstimada;
            response.estado = advisory.estado;
            response.modalidad = advisory.modalidad;
            response.ubicacion = advisory.ubicacion;
            response.linkReunion = advisory.linkReunion;
            response.notasAgricultor = advisory.notasAgricultor;
            response.notasExperto = advisory.notasExperto;
            response.fechaCreacion = advisory.fechaCreacion;
            response.fechaActualizacion = advisory.fechaActualizacion;
            return response;
        }
    }

    public static class ApiResponse<T> {
        public boolean success;
        public String message;
        public T data;

        public ApiResponse(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public static <T> ApiResponse<T> success(String message, T data) {
            return new ApiResponse<>(true, message, data);
        }

        public static <T> ApiResponse<T> error(String message) {
            return new ApiResponse<>(false, message, null);
        }
    }
}