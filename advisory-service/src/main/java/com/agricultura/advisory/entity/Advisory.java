package com.agricultura.advisory.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "advisories")
public class Advisory extends PanacheEntity {

    @Column(name = "agricultor_username", nullable = false)
    public String agricultorUsername;

    @Column(name = "agricultor_email", nullable = false)
    public String agricultorEmail;

    @Column(name = "agricultor_name", nullable = false)
    public String agricultorName;

    @Column(name = "experto_username")
    public String expertoUsername;

    @Column(name = "experto_email")
    public String expertoEmail;

    @Column(name = "experto_name")
    public String expertoName;

    @Column(name = "titulo", nullable = false, length = 200)
    public String titulo;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    public String descripcion;

    @Column(name = "tipo_asesoria", nullable = false)
    public String tipoAsesoria; // CULTIVO, PLAGAS, FERTILIZACION, RIEGO, GENERAL

    @Column(name = "fecha_solicitada", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime fechaSolicitada;

    @Column(name = "fecha_propuesta")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime fechaPropuesta;

    @Column(name = "duracion_estimada")
    public Integer duracionEstimada; // en minutos

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    public EstadoAsesoria estado = EstadoAsesoria.PENDIENTE;

    @Enumerated(EnumType.STRING)
    @Column(name = "modalidad", nullable = false)
    public ModalidadAsesoria modalidad;

    @Column(name = "ubicacion")
    public String ubicacion; // Para modalidad presencial

    @Column(name = "link_reunion")
    public String linkReunion; // Para modalidad virtual

    @Column(name = "notas_agricultor", columnDefinition = "TEXT")
    public String notasAgricultor;

    @Column(name = "notas_experto", columnDefinition = "TEXT")
    public String notasExperto;

    @Column(name = "fecha_creacion", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime fechaActualizacion;

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    public enum EstadoAsesoria {
        PENDIENTE,
        ACEPTADA,
        RECHAZADA,
        PROGRAMADA,
        EN_CURSO,
        COMPLETADA,
        CANCELADA
    }

    public enum ModalidadAsesoria {
        PRESENCIAL,
        VIRTUAL,
        TELEFONICA
    }
}