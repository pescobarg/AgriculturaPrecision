import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { interval, Subscription } from 'rxjs';
import { Chart, ChartConfiguration, ChartOptions, registerables } from 'chart.js';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

// Registrar Chart.js
Chart.register(...registerables);

interface SensorData {
  id: string;
  name: string;
  type: SensorType;
  value: number;
  unit: string;
  status: 'normal' | 'warning' | 'critical';
  lastUpdate: Date;
  history: { timestamp: Date; value: number }[];
  minThreshold?: number;
  maxThreshold?: number;
}

interface Alert {
  id: string;
  sensorId: string;
  sensorName: string;
  message: string;
  severity: 'info' | 'warning' | 'error';
  timestamp: Date;
  acknowledged: boolean;
}

enum SensorType {
  TEMPERATURE = 'Temperatura',
  HUMIDITY = 'Humedad',
  SOIL_MOISTURE = 'Humedad del Suelo',
  PH = 'pH del Suelo',
  LIGHT = 'Luz Solar',
  WIND_SPEED = 'Velocidad del Viento',
  RAINFALL = 'Precipitación',
  CO2 = 'CO2',
  CONDUCTIVITY = 'Conductividad Eléctrica'
}

@Component({
  selector: 'app-sensores',
  imports: [CommonModule, FormsModule],
  templateUrl: './sensores.component.html',
  styleUrl: './sensores.component.css'
})
export class SensoresComponent implements OnInit, OnDestroy {
  sensors: SensorData[] = [];
  alerts: Alert[] = [];
  charts: { [key: string]: Chart } = {};
  updateSubscription?: Subscription;
  
  // Modal states
  showAddSensorModal = false;
  newSensorType: SensorType = SensorType.TEMPERATURE;
  newSensorName = '';
  
  // Available sensor types
  sensorTypes = Object.values(SensorType);
  SensorType = SensorType;

  constructor(private router: Router) {}

  ngOnInit() {
    this.initializeSensors();
    this.startDataUpdates();
    setTimeout(() => this.initializeCharts(), 100);
  }

  ngOnDestroy() {
    this.updateSubscription?.unsubscribe();
    Object.values(this.charts).forEach(chart => chart.destroy());
  }

  private initializeSensors() {
    const defaultSensors: Omit<SensorData, 'id' | 'history' | 'lastUpdate'>[] = [
      {
        name: 'Sensor Temperatura Ambiente',
        type: SensorType.TEMPERATURE,
        value: 25.5,
        unit: '°C',
        status: 'normal',
        minThreshold: 15,
        maxThreshold: 35
      },
      {
        name: 'Sensor Humedad Relativa',
        type: SensorType.HUMIDITY,
        value: 68,
        unit: '%',
        status: 'normal',
        minThreshold: 40,
        maxThreshold: 80
      },
      {
        name: 'Sensor Humedad Suelo',
        type: SensorType.SOIL_MOISTURE,
        value: 45,
        unit: '%',
        status: 'warning',
        minThreshold: 30,
        maxThreshold: 70
      },
      {
        name: 'Sensor pH Suelo',
        type: SensorType.PH,
        value: 6.8,
        unit: 'pH',
        status: 'normal',
        minThreshold: 6.0,
        maxThreshold: 7.5
      }
    ];

    this.sensors = defaultSensors.map((sensor, index) => ({
      ...sensor,
      id: `sensor-${index + 1}`,
      lastUpdate: new Date(),
      history: this.generateInitialHistory()
    }));
  }

  private generateInitialHistory(): { timestamp: Date; value: number }[] {
    const history = [];
    const now = new Date();
    for (let i = 23; i >= 0; i--) {
      const timestamp = new Date(now.getTime() - i * 60 * 60 * 1000);
      history.push({
        timestamp,
        value: Math.random() * 50 + 25
      });
    }
    return history;
  }

  private startDataUpdates() {
    this.updateSubscription = interval(5000).subscribe(() => {
      this.updateSensorData();
    });
  }

  private updateSensorData() {
    this.sensors.forEach(sensor => {
      // Simular cambios realistas en los datos
      const variation = (Math.random() - 0.5) * 2;
      let newValue = sensor.value + variation;
      
      // Mantener valores dentro de rangos realistas
      switch (sensor.type) {
        case SensorType.TEMPERATURE:
          newValue = Math.max(0, Math.min(50, newValue));
          break;
        case SensorType.HUMIDITY:
        case SensorType.SOIL_MOISTURE:
          newValue = Math.max(0, Math.min(100, newValue));
          break;
        case SensorType.PH:
          newValue = Math.max(4, Math.min(10, newValue));
          break;
        default:
          newValue = Math.max(0, newValue);
      }

      sensor.value = Math.round(newValue * 10) / 10;
      sensor.lastUpdate = new Date();
      
      // Actualizar historial
      sensor.history.push({
        timestamp: new Date(),
        value: sensor.value
      });
      
      // Mantener solo las últimas 24 horas
      if (sensor.history.length > 24) {
        sensor.history.shift();
      }

      // Verificar umbrales y generar alertas
      this.checkThresholds(sensor);
      
      // Actualizar estado
      this.updateSensorStatus(sensor);
    });

    // Actualizar gráficas
    this.updateCharts();
  }

  private checkThresholds(sensor: SensorData) {
    if (!sensor.minThreshold || !sensor.maxThreshold) return;

    if (sensor.value < sensor.minThreshold || sensor.value > sensor.maxThreshold) {
      const existingAlert = this.alerts.find(
        alert => alert.sensorId === sensor.id && !alert.acknowledged
      );

      if (!existingAlert) {
        this.createAlert(sensor);
      }
    }
  }

  private createAlert(sensor: SensorData) {
    const severity = sensor.value < (sensor.minThreshold || 0) - 5 || 
                    sensor.value > (sensor.maxThreshold || 100) + 5 ? 'error' : 'warning';
    
    const alert: Alert = {
      id: `alert-${Date.now()}`,
      sensorId: sensor.id,
      sensorName: sensor.name,
      message: `${sensor.name}: Valor ${sensor.value}${sensor.unit} fuera del rango normal`,
      severity,
      timestamp: new Date(),
      acknowledged: false
    };

    this.alerts.unshift(alert);
    
    // Mantener solo las últimas 50 alertas
    if (this.alerts.length > 50) {
      this.alerts = this.alerts.slice(0, 50);
    }
  }

  private updateSensorStatus(sensor: SensorData) {
    if (!sensor.minThreshold || !sensor.maxThreshold) {
      sensor.status = 'normal';
      return;
    }

    if (sensor.value < sensor.minThreshold || sensor.value > sensor.maxThreshold) {
      sensor.status = 'critical';
    } else if (
      sensor.value < sensor.minThreshold + 5 || 
      sensor.value > sensor.maxThreshold - 5
    ) {
      sensor.status = 'warning';
    } else {
      sensor.status = 'normal';
    }
  }

  private initializeCharts() {
    this.sensors.forEach(sensor => {
      setTimeout(() => this.createChart(sensor), 100);
    });
  }

  private createChart(sensor: SensorData) {
    const canvas = document.getElementById(`chart-${sensor.id}`) as HTMLCanvasElement;
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    if (this.charts[sensor.id]) {
      this.charts[sensor.id].destroy();
    }

    const config: ChartConfiguration = {
      type: 'line',
      data: {
        labels: sensor.history.map(h => 
          h.timestamp.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' })
        ),
        datasets: [{
          label: sensor.name,
          data: sensor.history.map(h => h.value),
          borderColor: this.getChartColor(sensor.type),
          backgroundColor: this.getChartColor(sensor.type, 0.1),
          tension: 0.4,
          fill: true
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false
          }
        },
        scales: {
          y: {
            beginAtZero: sensor.type !== SensorType.PH,
            grid: {
              color: 'rgba(0,0,0,0.1)'
            }
          },
          x: {
            grid: {
              color: 'rgba(0,0,0,0.1)'
            }
          }
        },
        elements: {
          point: {
            radius: 2
          }
        }
      }
    };

    this.charts[sensor.id] = new Chart(ctx, config);
  }

  private updateCharts() {
    this.sensors.forEach(sensor => {
      const chart = this.charts[sensor.id];
      if (chart) {
        chart.data.labels = sensor.history.map(h => 
          h.timestamp.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' })
        );
        chart.data.datasets[0].data = sensor.history.map(h => h.value);
        chart.update('none');
      }
    });
  }

  private getChartColor(sensorType: SensorType, alpha: number = 1): string {
    const colors = {
      [SensorType.TEMPERATURE]: `rgba(255, 99, 132, ${alpha})`,
      [SensorType.HUMIDITY]: `rgba(54, 162, 235, ${alpha})`,
      [SensorType.SOIL_MOISTURE]: `rgba(75, 192, 192, ${alpha})`,
      [SensorType.PH]: `rgba(153, 102, 255, ${alpha})`,
      [SensorType.LIGHT]: `rgba(255, 206, 86, ${alpha})`,
      [SensorType.WIND_SPEED]: `rgba(201, 203, 207, ${alpha})`,
      [SensorType.RAINFALL]: `rgba(75, 192, 192, ${alpha})`,
      [SensorType.CO2]: `rgba(255, 159, 64, ${alpha})`,
      [SensorType.CONDUCTIVITY]: `rgba(199, 199, 199, ${alpha})`
    };
    return colors[sensorType];
  }

  // Métodos públicos para el template
  goToDashboard() {
    this.router.navigate(['/dashboard-agricultor']);
  }

  openAddSensorModal() {
    this.showAddSensorModal = true;
    this.newSensorName = '';
    this.newSensorType = SensorType.TEMPERATURE;
  }

  closeAddSensorModal() {
    this.showAddSensorModal = false;
  }

  addSensor() {
    if (!this.newSensorName.trim()) return;

    const newSensor: SensorData = {
      id: `sensor-${Date.now()}`,
      name: this.newSensorName.trim(),
      type: this.newSensorType,
      value: this.getInitialValue(this.newSensorType),
      unit: this.getUnit(this.newSensorType),
      status: 'normal',
      lastUpdate: new Date(),
      history: this.generateInitialHistory(),
      minThreshold: this.getMinThreshold(this.newSensorType),
      maxThreshold: this.getMaxThreshold(this.newSensorType)
    };

    this.sensors.push(newSensor);
    this.closeAddSensorModal();
    
    // Crear gráfica para el nuevo sensor
    setTimeout(() => this.createChart(newSensor), 100);
  }

  private getInitialValue(type: SensorType): number {
    const values = {
      [SensorType.TEMPERATURE]: 22,
      [SensorType.HUMIDITY]: 65,
      [SensorType.SOIL_MOISTURE]: 50,
      [SensorType.PH]: 6.5,
      [SensorType.LIGHT]: 800,
      [SensorType.WIND_SPEED]: 5,
      [SensorType.RAINFALL]: 0,
      [SensorType.CO2]: 400,
      [SensorType.CONDUCTIVITY]: 1.2
    };
    return values[type];
  }

  private getUnit(type: SensorType): string {
    const units = {
      [SensorType.TEMPERATURE]: '°C',
      [SensorType.HUMIDITY]: '%',
      [SensorType.SOIL_MOISTURE]: '%',
      [SensorType.PH]: 'pH',
      [SensorType.LIGHT]: 'lux',
      [SensorType.WIND_SPEED]: 'm/s',
      [SensorType.RAINFALL]: 'mm',
      [SensorType.CO2]: 'ppm',
      [SensorType.CONDUCTIVITY]: 'mS/cm'
    };
    return units[type];
  }

  private getMinThreshold(type: SensorType): number {
    const thresholds = {
      [SensorType.TEMPERATURE]: 15,
      [SensorType.HUMIDITY]: 40,
      [SensorType.SOIL_MOISTURE]: 30,
      [SensorType.PH]: 6.0,
      [SensorType.LIGHT]: 500,
      [SensorType.WIND_SPEED]: 0,
      [SensorType.RAINFALL]: 0,
      [SensorType.CO2]: 300,
      [SensorType.CONDUCTIVITY]: 0.5
    };
    return thresholds[type];
  }

  private getMaxThreshold(type: SensorType): number {
    const thresholds = {
      [SensorType.TEMPERATURE]: 35,
      [SensorType.HUMIDITY]: 80,
      [SensorType.SOIL_MOISTURE]: 70,
      [SensorType.PH]: 7.5,
      [SensorType.LIGHT]: 1200,
      [SensorType.WIND_SPEED]: 15,
      [SensorType.RAINFALL]: 50,
      [SensorType.CO2]: 800,
      [SensorType.CONDUCTIVITY]: 3.0
    };
    return thresholds[type];
  }

  deleteSensor(sensorId: string) {
    if (confirm('¿Está seguro de que desea eliminar este sensor?')) {
      // Destruir gráfica
      if (this.charts[sensorId]) {
        this.charts[sensorId].destroy();
        delete this.charts[sensorId];
      }
      
      // Eliminar sensor
      this.sensors = this.sensors.filter(s => s.id !== sensorId);
      
      // Eliminar alertas relacionadas
      this.alerts = this.alerts.filter(a => a.sensorId !== sensorId);
    }
  }

  acknowledgeAlert(alertId: string) {
    const alert = this.alerts.find(a => a.id === alertId);
    if (alert) {
      alert.acknowledged = true;
    }
  }

  getSeverityIcon(severity: string): string {
    const icons = {
      'info': 'ℹ️',
      'warning': '⚠️',
      'error': '🚨'
    };
    return icons[severity as keyof typeof icons] || 'ℹ️';
  }

  getStatusIcon(status: string): string {
    const icons = {
      'normal': '✅',
      'warning': '⚠️',
      'critical': '🚨'
    };
    return icons[status as keyof typeof icons] || '✅';
  }

generateReport() {
  const doc = new jsPDF();
  const now = new Date();

  doc.setFontSize(20);
  doc.text('Reporte de Sensores Agrícolas', 20, 20);

  doc.setFontSize(12);
  doc.text(`Generado: ${now.toLocaleString('es-ES')}`, 20, 35);

  doc.setFontSize(14);
  doc.text('Resumen General', 20, 50);
  doc.setFontSize(10);
  doc.text(`Total de sensores: ${this.sensors.length}`, 20, 60);
  doc.text(`Alertas activas: ${this.alerts.filter(a => !a.acknowledged).length}`, 20, 70);

  const sensorData = this.sensors.map(sensor => [
    sensor.name,
    sensor.type,
    `${sensor.value} ${sensor.unit}`,
    sensor.status,
    sensor.lastUpdate.toLocaleString('es-ES')
  ]);

  autoTable(doc, {
    head: [['Nombre', 'Tipo', 'Valor Actual', 'Estado', 'Última Actualización']],
    body: sensorData,
    startY: 80,
    styles: { fontSize: 8 }
  });

  if (this.alerts.length > 0) {
    const finalY = (doc as any).lastAutoTable.finalY + 10;
    doc.setFontSize(14);
    doc.text('Alertas Recientes', 20, finalY);

    const alertData = this.alerts.slice(0, 10).map(alert => [
      alert.sensorName,
      alert.message,
      alert.severity,
      alert.timestamp.toLocaleString('es-ES'),
      alert.acknowledged ? 'Sí' : 'No'
    ]);

    autoTable(doc, {
      head: [['Sensor', 'Mensaje', 'Severidad', 'Fecha', 'Reconocida']],
      body: alertData,
      startY: finalY + 10,
      styles: { fontSize: 8 }
    });
  }

  doc.save(`reporte-sensores-${now.toISOString().split('T')[0]}.pdf`);
}

  get activeSensorsCount(): number {
  return this.sensors.length;
}

get warningAlertsCount(): number {
  return this.alerts.filter(a => !a.acknowledged && a.severity === 'warning').length;
}

get criticalAlertsCount(): number {
  return this.alerts.filter(a => !a.acknowledged && a.severity === 'error').length;
}

get normalSensorsCount(): number {
  return this.sensors.filter(s => s.status === 'normal').length;
}

get recentAlerts(): any[] {
  return this.alerts.slice(0, 5);
}

}