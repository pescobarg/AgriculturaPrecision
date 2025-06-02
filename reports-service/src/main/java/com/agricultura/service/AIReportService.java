package com.agricultura.service;

import com.agricultura.dto.SensorDataDto;
import com.agricultura.dto.SensorStatsDto;
import com.agricultura.entity.Report;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

@ApplicationScoped
public class AIReportService {

    private static final Logger LOG = Logger.getLogger(AIReportService.class);

    // Rangos óptimos para cultivos
    private static final double OPTIMAL_TEMP_MIN = 18.0;
    private static final double OPTIMAL_TEMP_MAX = 28.0;
    private static final double OPTIMAL_HUMIDITY_MIN = 60.0;
    private static final double OPTIMAL_HUMIDITY_MAX = 80.0;

    public String generateTemperatureAnalysis(List<SensorDataDto> sensorData, SensorStatsDto stats) {
        LOG.info("🤖 Generando análisis de temperatura con IA");
        
        StringBuilder analysis = new StringBuilder();
        List<String> recommendations = new ArrayList<>();
        
        // Análisis de temperatura promedio
        double avgTemp = stats.averageTemperature != null ? stats.averageTemperature : 0.0;
        
        analysis.append("ANÁLISIS DE TEMPERATURA\n");
        analysis.append("=====================\n\n");
        
        analysis.append(String.format("📊 Temperatura promedio: %.2f°C\n", avgTemp));
        analysis.append(String.format("🌡️ Temperatura máxima: %s°C\n", 
            stats.maxTemperature != null ? stats.maxTemperature : "N/A"));
        analysis.append(String.format("🌡️ Temperatura mínima: %s°C\n", 
            stats.minTemperature != null ? stats.minTemperature : "N/A"));
        analysis.append(String.format("📈 Total de registros analizados: %d\n\n", 
            stats.totalRecords != null ? stats.totalRecords : 0));

        // Evaluación de rangos óptimos
        if (avgTemp < OPTIMAL_TEMP_MIN) {
            analysis.append("⚠️ ALERTA: Temperatura promedio por debajo del rango óptimo\n");
            analysis.append("La temperatura actual está afectando el crecimiento de los cultivos.\n\n");
            recommendations.add("Implementar sistemas de calefacción o invernaderos");
            recommendations.add("Considerar cultivos resistentes al frío");
        } else if (avgTemp > OPTIMAL_TEMP_MAX) {
            analysis.append("🔥 ALERTA: Temperatura promedio por encima del rango óptimo\n");
            analysis.append("El estrés térmico puede reducir la productividad de los cultivos.\n\n");
            recommendations.add("Implementar sistemas de enfriamiento o sombreado");
            recommendations.add("Aumentar la frecuencia de riego");
        } else {
            analysis.append("✅ Temperatura dentro del rango óptimo para la mayoría de cultivos\n\n");
        }

        // Análisis de variabilidad
        if (stats.maxTemperature != null && stats.minTemperature != null) {
            double tempRange = stats.maxTemperature.doubleValue() - stats.minTemperature.doubleValue();
            analysis.append(String.format("📊 Variabilidad térmica: %.2f°C\n", tempRange));
            
            if (tempRange > 15.0) {
                analysis.append("⚠️ Alta variabilidad térmica detectada\n");
                recommendations.add("Implementar sistemas de control térmico más estables");
            }
        }

        // Añadir recomendaciones
        if (!recommendations.isEmpty()) {
            analysis.append("\n🎯 RECOMENDACIONES:\n");
            for (int i = 0; i < recommendations.size(); i++) {
                analysis.append(String.format("%d. %s\n", i + 1, recommendations.get(i)));
            }
        }

        return analysis.toString();
    }

    public String generateHumidityAnalysis(List<SensorDataDto> sensorData, SensorStatsDto stats) {
        LOG.info("🤖 Generando análisis de humedad con IA");
        
        StringBuilder analysis = new StringBuilder();
        List<String> recommendations = new ArrayList<>();
        
        double avgHumidity = stats.averageHumidity != null ? stats.averageHumidity : 0.0;
        
        analysis.append("ANÁLISIS DE HUMEDAD\n");
        analysis.append("==================\n\n");
        
        analysis.append(String.format("💧 Humedad promedio: %.2f%%\n", avgHumidity));
        analysis.append(String.format("💧 Humedad máxima: %s%%\n", 
            stats.maxHumidity != null ? stats.maxHumidity : "N/A"));
        analysis.append(String.format("💧 Humedad mínima: %s%%\n", 
            stats.minHumidity != null ? stats.minHumidity : "N/A"));
        analysis.append(String.format("📈 Total de registros analizados: %d\n\n", 
            stats.totalRecords != null ? stats.totalRecords : 0));

        // Evaluación de rangos óptimos
        if (avgHumidity < OPTIMAL_HUMIDITY_MIN) {
            analysis.append("🏜️ ALERTA: Humedad por debajo del rango óptimo\n");
            analysis.append("Ambiente demasiado seco puede causar estrés hídrico en los cultivos.\n\n");
            recommendations.add("Aumentar la frecuencia de riego");
            recommendations.add("Implementar sistemas de nebulización");
            recommendations.add("Añadir mulch para conservar humedad del suelo");
        } else if (avgHumidity > OPTIMAL_HUMIDITY_MAX) {
            analysis.append("🌊 ALERTA: Humedad por encima del rango óptimo\n");
            analysis.append("Exceso de humedad puede favorecer enfermedades fúngicas.\n\n");
            recommendations.add("Mejorar la ventilación del área de cultivo");
            recommendations.add("Reducir la frecuencia de riego");
            recommendations.add("Aplicar fungicidas preventivos");
        } else {
            analysis.append("✅ Humedad dentro del rango óptimo para la mayoría de cultivos\n\n");
        }

        // Análisis de variabilidad
        if (stats.maxHumidity != null && stats.minHumidity != null) {
            double humidityRange = stats.maxHumidity.doubleValue() - stats.minHumidity.doubleValue();
            analysis.append(String.format("📊 Variabilidad en humedad: %.2f%%\n", humidityRange));
            
            if (humidityRange > 40.0) {
                analysis.append("⚠️ Alta variabilidad en humedad detectada\n");
                recommendations.add("Implementar sistemas de control de humedad más estables");
            }
        }

        // Añadir recomendaciones
        if (!recommendations.isEmpty()) {
            analysis.append("\n🎯 RECOMENDACIONES:\n");
            for (int i = 0; i < recommendations.size(); i++) {
                analysis.append(String.format("%d. %s\n", i + 1, recommendations.get(i)));
            }
        }

        return analysis.toString();
    }

    public String generateGeneralAnalysis(List<SensorDataDto> sensorData, SensorStatsDto stats) {
        LOG.info("🤖 Generando análisis general con IA");
        
        StringBuilder analysis = new StringBuilder();
        List<String> recommendations = new ArrayList<>();
        
        double avgTemp = stats.averageTemperature != null ? stats.averageTemperature : 0.0;
        double avgHumidity = stats.averageHumidity != null ? stats.averageHumidity : 0.0;
        
        analysis.append("ANÁLISIS GENERAL DEL CULTIVO\n");
        analysis.append("===========================\n\n");
        
        analysis.append("📊 RESUMEN DE CONDICIONES:\n");
        analysis.append(String.format("🌡️ Temperatura promedio: %.2f°C\n", avgTemp));
        analysis.append(String.format("💧 Humedad promedio: %.2f%%\n", avgHumidity));
        analysis.append(String.format("📈 Registros analizados: %d\n", 
            stats.totalRecords != null ? stats.totalRecords : 0));
        analysis.append(String.format("📅 Período: %s a %s\n\n", 
            stats.fromDate != null ? stats.fromDate : "N/A",
            stats.toDate != null ? stats.toDate : "N/A"));

        // Evaluación combinada
        boolean tempOptimal = avgTemp >= OPTIMAL_TEMP_MIN && avgTemp <= OPTIMAL_TEMP_MAX;
        boolean humidityOptimal = avgHumidity >= OPTIMAL_HUMIDITY_MIN && avgHumidity <= OPTIMAL_HUMIDITY_MAX;

        if (tempOptimal && humidityOptimal) {
            analysis.append("✅ CONDICIONES ÓPTIMAS: El ambiente está dentro de los parámetros ideales\n");
            analysis.append("Los cultivos deberían desarrollarse adecuadamente.\n\n");
        } else {
            analysis.append("⚠️ CONDICIONES REQUIEREN ATENCIÓN:\n");
            if (!tempOptimal) {
                analysis.append("- Temperatura fuera del rango óptimo\n");
            }
            if (!humidityOptimal) {
                analysis.append("- Humedad fuera del rango óptimo\n");
            }
            analysis.append("\n");
        }

        // Recomendaciones generales
        recommendations.add("Mantener monitoreo continuo de las condiciones ambientales");
        recommendations.add("Realizar calibración periódica de sensores");
        recommendations.add("Documentar cambios en el crecimiento de los cultivos");
        
        if (!tempOptimal || !humidityOptimal) {
            recommendations.add("Implementar sistema de alertas para condiciones críticas");
            recommendations.add("Considerar ajustes en las prácticas de cultivo");
        }

        // Añadir recomendaciones
        analysis.append("🎯 RECOMENDACIONES GENERALES:\n");
        for (int i = 0; i < recommendations.size(); i++) {
            analysis.append(String.format("%d. %s\n", i + 1, recommendations.get(i)));
        }

        return analysis.toString();
    }

    public int detectAnomalies(List<SensorDataDto> sensorData, SensorStatsDto stats) {
        LOG.info("🔍 Detectando anomalías en los datos");
        
        int anomalies = 0;
        double avgTemp = stats.averageTemperature != null ? stats.averageTemperature : 0.0;
        double avgHumidity = stats.averageHumidity != null ? stats.averageHumidity : 0.0;

        for (SensorDataDto data : sensorData) {
            if (data.temperatura != null) {
                double temp = data.temperatura.doubleValue();
                // Detectar temperaturas extremas (más de 2 desviaciones estándar)
                if (Math.abs(temp - avgTemp) > 10.0) {
                    anomalies++;
                }
            }
            
            if (data.humedad != null) {
                double humidity = data.humedad.doubleValue();
                // Detectar humedad extrema
                if (Math.abs(humidity - avgHumidity) > 30.0) {
                    anomalies++;
                }
            }
        }

        LOG.infof("🔍 Detectadas %d anomalías en %d registros", anomalies, sensorData.size());
        return anomalies;
    }

    public String generateRecommendations(List<SensorDataDto> sensorData, SensorStatsDto stats) {
        LOG.info("💡 Generando recomendaciones personalizadas");
        
        StringBuilder recommendations = new StringBuilder();
        double avgTemp = stats.averageTemperature != null ? stats.averageTemperature : 0.0;
        double avgHumidity = stats.averageHumidity != null ? stats.averageHumidity : 0.0;

        recommendations.append("RECOMENDACIONES PERSONALIZADAS\n");
        recommendations.append("=============================\n\n");

        // Recomendaciones basadas en temperatura
        if (avgTemp < OPTIMAL_TEMP_MIN) {
            recommendations.append("🌡️ TEMPERATURA BAJA:\n");
            recommendations.append("- Considerar el uso de mantas térmicas durante la noche\n");
            recommendations.append("- Implementar calefacción auxiliar en invernaderos\n");
            recommendations.append("- Seleccionar variedades resistentes al frío\n\n");
        } else if (avgTemp > OPTIMAL_TEMP_MAX) {
            recommendations.append("🔥 TEMPERATURA ALTA:\n");
            recommendations.append("- Instalar mallas de sombreo (30-50%)\n");
            recommendations.append("- Implementar sistemas de ventilación forzada\n");
            recommendations.append("- Programar riegos en horarios más frescos\n\n");
        }

        // Recomendaciones basadas en humedad
        if (avgHumidity < OPTIMAL_HUMIDITY_MIN) {
            recommendations.append("🏜️ HUMEDAD BAJA:\n");
            recommendations.append("- Instalar sistemas de nebulización\n");
            recommendations.append("- Aplicar mulch orgánico para retener humedad\n");
            recommendations.append("- Aumentar frecuencia de riegos ligeros\n\n");
        } else if (avgHumidity > OPTIMAL_HUMIDITY_MAX) {
            recommendations.append("🌊 HUMEDAD ALTA:\n");
            recommendations.append("- Mejorar drenaje del suelo\n");
            recommendations.append("- Aumentar espaciamiento entre plantas\n");
            recommendations.append("- Aplicar tratamientos fungicidas preventivos\n\n");
        }

        // Recomendaciones generales
        recommendations.append("📋 MEJORES PRÁCTICAS:\n");
        recommendations.append("- Revisar y calibrar sensores semanalmente\n");
        recommendations.append("- Mantener registro detallado de condiciones climáticas\n");
        recommendations.append("- Implementar sistema de alertas automáticas\n");
        recommendations.append("- Realizar análisis de suelo trimestral\n");

        return recommendations.toString();
    }
}
