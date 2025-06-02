package com.agricultura.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sensor_data")
public class SensorData extends PanacheEntity {

    @Column(nullable = false, precision = 5, scale = 2)
    public BigDecimal temperatura;

    @Column(nullable = false, precision = 5, scale = 2)
    public BigDecimal humedad;

    @Column(name = "timestamp", nullable = false)
    public LocalDateTime timestamp;

    @Column(name = "received_at", nullable = false)
    public LocalDateTime receivedAt;

    @PrePersist
    public void prePersist() {
        receivedAt = LocalDateTime.now();
    }

    // Método para buscar datos por rango de fechas
    public static List<SensorData> findByDateRange(LocalDateTime from, LocalDateTime to) {
        return find("timestamp >= ?1 and timestamp <= ?2 order by timestamp desc", from, to).list();
    }

    // Método para obtener los últimos N registros
    public static List<SensorData> findLatest(int limit) {
        return find("order by timestamp desc").page(0, limit).list();
    }

    // Método para obtener el promedio de temperatura en un rango
    public static Double getAverageTemperature(LocalDateTime from, LocalDateTime to) {
        return getEntityManager()
            .createQuery("SELECT AVG(s.temperatura) FROM SensorData s WHERE s.timestamp >= :from AND s.timestamp <= :to", Double.class)
            .setParameter("from", from)
            .setParameter("to", to)
            .getSingleResult();
    }

    // Método para obtener el promedio de humedad en un rango
    public static Double getAverageHumidity(LocalDateTime from, LocalDateTime to) {
        return getEntityManager()
            .createQuery("SELECT AVG(s.humedad) FROM SensorData s WHERE s.timestamp >= :from AND s.timestamp <= :to", Double.class)
            .setParameter("from", from)
            .setParameter("to", to)
            .getSingleResult();
    }
}