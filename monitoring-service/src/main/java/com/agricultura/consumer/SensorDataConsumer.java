package com.agricultura.consumer;

import com.agricultura.dto.SensorDataDto;
import com.agricultura.entity.SensorData;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class SensorDataConsumer {

    private static final Logger LOG = Logger.getLogger(SensorDataConsumer.class);
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Inject
    ObjectMapper objectMapper;

   @Incoming("sensor-data")
    @Transactional
    public void consume(byte[] messageBytes) {
        try {
            String message = new String(messageBytes); // Conversión segura
            LOG.debugf("📨 Mensaje recibido: %s", message);

            // Deserializar el mensaje JSON
            SensorDataDto sensorDto = objectMapper.readValue(message, SensorDataDto.class);
            LOG.debugf("🔄 DTO deserializado: %s", sensorDto);

            // Crear la entidad SensorData
            SensorData sensorData = new SensorData();
            sensorData.temperatura = sensorDto.temperatura;
            sensorData.humedad = sensorDto.humedad;

            // Parsear el timestamp
            try {
                sensorData.timestamp = LocalDateTime.parse(sensorDto.timestamp, ISO_FORMATTER);
            } catch (Exception e) {
                LOG.warnf("⚠️ Error parseando timestamp '%s', usando tiempo actual: %s", 
                    sensorDto.timestamp, e.getMessage());
                sensorData.timestamp = LocalDateTime.now();
            }

            // Guardar en la base de datos
            sensorData.persist();

            LOG.infof("✅ Datos del sensor guardados: ID=%d, T=%.2f°C, H=%.2f%%, Timestamp=%s", 
                sensorData.id, 
                sensorData.temperatura, 
                sensorData.humedad, 
                sensorData.timestamp);

        } catch (Exception e) {
            LOG.errorf(e, "❌ Error procesando mensaje del sensor");
        }
    }

}