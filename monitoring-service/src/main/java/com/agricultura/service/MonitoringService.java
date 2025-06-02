package com.agricultura.service;

import com.agricultura.entity.SensorData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@ApplicationScoped
public class MonitoringService {

    private static final Logger LOG = Logger.getLogger(MonitoringService.class);

    @Transactional
    public List<SensorData> getAllSensorData() {
        LOG.debug("🔍 Obteniendo todos los datos del sensor");
        return SensorData.listAll();
    }

    @Transactional
    public List<SensorData> getLatestSensorData(int limit) {
        LOG.debugf("🔍 Obteniendo los últimos %d registros", limit);
        return SensorData.findLatest(limit);
    }

    @Transactional
    public List<SensorData> getSensorDataByDateRange(LocalDateTime from, LocalDateTime to) {
        LOG.debugf("🔍 Obteniendo datos entre %s y %s", from, to);
        return SensorData.findByDateRange(from, to);
    }

    @Transactional
    public Map<String, Object> getSensorStats(LocalDateTime from, LocalDateTime to) {
        LOG.debugf("📊 Calculando estadísticas entre %s y %s", from, to);
        
        Map<String, Object> stats = new HashMap<>();
        
        try {
            Double avgTemp = SensorData.getAverageTemperature(from, to);
            Double avgHumidity = SensorData.getAverageHumidity(from, to);
            long count = SensorData.count("timestamp >= ?1 and timestamp <= ?2", from, to);
            
            stats.put("averageTemperature", avgTemp != null ? Math.round(avgTemp * 100.0) / 100.0 : 0.0);
            stats.put("averageHumidity", avgHumidity != null ? Math.round(avgHumidity * 100.0) / 100.0 : 0.0);
            stats.put("totalRecords", count);
            stats.put("fromDate", from);
            stats.put("toDate", to);
            
            // Obtener valores mínimos y máximos
            SensorData maxTemp = SensorData.find("timestamp >= ?1 and timestamp <= ?2 order by temperatura desc", from, to)
                .firstResult();
            SensorData minTemp = SensorData.find("timestamp >= ?1 and timestamp <= ?2 order by temperatura asc", from, to)
                .firstResult();
            SensorData maxHumidity = SensorData.find("timestamp >= ?1 and timestamp <= ?2 order by humedad desc", from, to)
                .firstResult();
            SensorData minHumidity = SensorData.find("timestamp >= ?1 and timestamp <= ?2 order by humedad asc", from, to)
                .firstResult();
            
            if (maxTemp != null) stats.put("maxTemperature", maxTemp.temperatura);
            if (minTemp != null) stats.put("minTemperature", minTemp.temperatura);
            if (maxHumidity != null) stats.put("maxHumidity", maxHumidity.humedad);
            if (minHumidity != null) stats.put("minHumidity", minHumidity.humedad);
            
        } catch (Exception e) {
            LOG.errorf(e, "❌ Error calculando estadísticas");
            stats.put("error", "Error calculando estadísticas: " + e.getMessage());
        }
        
        return stats;
    }

    @Transactional
    public long getTotalRecords() {
        return SensorData.count();
    }

    @Transactional
    public void deleteOldRecords(LocalDateTime before) {
        LOG.debugf("🗑️ Eliminando registros anteriores a %s", before);
        long deleted = SensorData.delete("timestamp < ?1", before);
        LOG.infof("✅ Eliminados %d registros antiguos", deleted);
    }
}