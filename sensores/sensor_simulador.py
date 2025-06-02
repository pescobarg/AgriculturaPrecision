import pika
import json
import random
import time
import os
from datetime import datetime

def wait_for_rabbitmq(host, max_retries=30, delay=5):
    """Espera a que RabbitMQ esté disponible"""
    for i in range(max_retries):
        try:
            connection = pika.BlockingConnection(pika.ConnectionParameters(host))
            connection.close()
            print(f"✅ Conexión exitosa a RabbitMQ en {host}")
            return True
        except Exception as e:
            print(f"⏳ Intento {i+1}/{max_retries} - RabbitMQ no disponible en {host}: {e}")
            time.sleep(delay)
    return False

def main():
    # Obtener configuración desde variables de entorno
    rabbitmq_host = os.getenv('RABBITMQ_HOST', 'localhost')
    queue_name = os.getenv('QUEUE_NAME', 'sensor_data')
    interval = int(os.getenv('SEND_INTERVAL', '2'))
    
    print(f"🚀 Iniciando simulador de sensores...")
    print(f"📡 RabbitMQ Host: {rabbitmq_host}")
    print(f"📦 Cola: {queue_name}")
    print(f"⏱️  Intervalo: {interval} segundos")
    
    # Esperar a que RabbitMQ esté disponible
    if not wait_for_rabbitmq(rabbitmq_host):
        print("❌ No se pudo conectar a RabbitMQ después de varios intentos")
        return
    
    # Configuración de conexión con RabbitMQ
    try:
        connection = pika.BlockingConnection(
            pika.ConnectionParameters(
                host=rabbitmq_host,
                heartbeat=600,
                blocked_connection_timeout=300,
            )
        )
        channel = connection.channel()
        
        # Crear la cola si no existe
        channel.queue_declare(queue=queue_name, durable=False)
        
        print(f"✅ Simulador de sensor iniciado correctamente")
        print(f"📊 Enviando datos cada {interval} segundos...")
        
        # Contador de mensajes enviados
        mensaje_count = 0
        
        # Loop para enviar datos
        while True:
            try:
                # Simulación de datos más realistas
                temperatura_base = 25.0
                humedad_base = 50.0
                
                # Añadir variación realista
                temperatura = round(temperatura_base + random.uniform(-5.0, 10.0), 2)
                humedad = round(humedad_base + random.uniform(-15.0, 25.0), 2)
                
                # Asegurar rangos realistas
                temperatura = max(15.0, min(40.0, temperatura))
                humedad = max(20.0, min(90.0, humedad))
                
                data = {
                    "temperatura": temperatura,
                    "humedad": humedad,
                    "timestamp": datetime.utcnow().isoformat()
                }
                
                # Serializar a JSON
                mensaje = json.dumps(data)
                
                # Enviar a la cola
                channel.basic_publish(
                    exchange='',
                    routing_key=queue_name,
                    body=mensaje,
                    properties=pika.BasicProperties(
                        delivery_mode=1,  # No persistir mensajes
                    )
                )
                
                mensaje_count += 1
                timestamp = datetime.now().strftime("%H:%M:%S")
                print(f"📤 [{timestamp}] Mensaje #{mensaje_count}: T={temperatura}°C, H={humedad}%")
                
                time.sleep(interval)
                
            except KeyboardInterrupt:
                print("\n🛑 Simulador detenido manualmente")
                break
            except Exception as e:
                print(f"❌ Error enviando mensaje: {e}")
                time.sleep(5)  # Esperar antes de reintentar
                
    except Exception as e:
        print(f"❌ Error de conexión: {e}")
    finally:
        try:
            if 'connection' in locals():
                connection.close()
            print("🔌 Conexión cerrada")
        except:
            pass

if __name__ == "__main__":
    main()