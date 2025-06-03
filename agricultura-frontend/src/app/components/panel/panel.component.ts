import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Subscription, interval } from 'rxjs';
import { switchMap, catchError } from 'rxjs/operators';
import { of } from 'rxjs';

interface PrometheusResponse {
  status: string;
  data: {
    resultType: string;
    result: Array<{
      metric: { [key: string]: string };
      value: [number, string];
    }>;
  };
}

interface GrafanaPanel {
  id: number;
  title: string;
  type: string;
  targets: Array<{
    expr: string;
    legendFormat: string;
  }>;
}

@Component({
  selector: 'app-panel',
  imports: [CommonModule],
  templateUrl: './panel.component.html',
  styleUrl: './panel.component.css'
})
export class PanelComponent implements OnInit, OnDestroy {
  @ViewChild('grafanaFrame', { static: false }) grafanaFrame!: ElementRef<HTMLIFrameElement>;

  // Configuración de URLs
  prometheusUrl = 'http://localhost:9090'; // URL de tu instancia Prometheus
  grafanaUrl = 'http://localhost:3000'; // URL de tu instancia Grafana
  
  // Datos de Prometheus
  prometheusData: any[] = [];
  metricsLoading = false;
  metricsError: string | null = null;
  
  // Configuración de Grafana
  grafanaDashboardId = 'c4d9759c-6f2c-41f3-bffb-ff1b5bdc1818'; // Se configurará después de crear el dashboard
  grafanaApiKey = ''; // Tu API key de Grafana
  
  // Panel embebido de Grafana
  grafanaEmbedUrl = '';
  showGrafanaPanel = false;
  grafanaLoaded = false;
  grafanaError = false;
  dashboardExists = false;
  
  // Subscripciones
  private dataSubscription?: Subscription;
  
  // Métricas de ejemplo
  prometheusQueries = [
    {
      name: 'CPU Usage',
      query: 'cpu_usage_percent',
      color: '#ff6b6b'
    },
    {
      name: 'Memory Usage',
      query: 'memory_usage_percent',
      color: '#4ecdc4'
    },
    {
      name: 'Disk Usage',
      query: 'disk_usage_percent',
      color: '#45b7d1'
    }
  ];

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.checkDashboardExists();
    this.startPrometheusDataFetch();
  }

  ngOnDestroy() {
    if (this.dataSubscription) {
      this.dataSubscription.unsubscribe();
    }
  }

  // Verificar si existe un dashboard
  checkDashboardExists() {
    if (this.grafanaDashboardId) {
      this.dashboardExists = true;
      this.setupGrafanaEmbed();
    } else {
      this.dashboardExists = false;
    }
  }

  // Configurar URL de embed de Grafana
  setupGrafanaEmbed() {
    if (!this.grafanaDashboardId) {
      return;
    }
    
    const panelId = 2; // ID del panel específico
    const from = 'now-1h'; // Últimas 1 hora
    const to = 'now';
    
    // Configuración para permitir iframe embedding
    const params = new URLSearchParams({
      'orgId': '1',
      'from': from,
      'to': to,
      'panelId': panelId.toString(),
      'theme': 'light', // Cambiar a light inicialmente
      'kiosk': 'tv', // Modo kiosco para mejor visualización
      'refresh': '30s'
    });
    
    this.grafanaEmbedUrl = `${this.grafanaUrl}/d-solo/${this.grafanaDashboardId}/dashboard?${params.toString()}`;
  }

  // Crear dashboard básico
  createBasicDashboard() {
    const dashboardConfig = {
      dashboard: {
        id: null,
        title: "Dashboard de Sistema",
        tags: ["prometheus", "angular"],
        timezone: "browser",
        panels: [
          {
            id: 1,
            title: "CPU Usage",
            type: "stat",
            targets: [
              {
                expr: "100 - (avg(irate(node_cpu_seconds_total{mode=\"idle\"}[5m])) * 100)",
                legendFormat: "CPU %",
                refId: "A"
              }
            ],
            fieldConfig: {
              defaults: {
                color: {
                  mode: "thresholds"
                },
                thresholds: {
                  mode: "absolute",
                  steps: [
                    { color: "green", value: null },
                    { color: "yellow", value: 60 },
                    { color: "red", value: 80 }
                  ]
                },
                unit: "percent"
              }
            },
            gridPos: { h: 8, w: 12, x: 0, y: 0 }
          },
          {
            id: 2,
            title: "Memory Usage",
            type: "stat",
            targets: [
              {
                expr: "(1 - (node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes)) * 100",
                legendFormat: "Memory %",
                refId: "A"
              }
            ],
            fieldConfig: {
              defaults: {
                color: {
                  mode: "thresholds"
                },
                thresholds: {
                  mode: "absolute",
                  steps: [
                    { color: "green", value: null },
                    { color: "yellow", value: 70 },
                    { color: "red", value: 85 }
                  ]
                },
                unit: "percent"
              }
            },
            gridPos: { h: 8, w: 12, x: 12, y: 0 }
          }
        ],
        time: {
          from: "now-1h",
          to: "now"
        },
        refresh: "30s"
      },
      overwrite: false
    };

    return JSON.stringify(dashboardConfig, null, 2);
  }

  // Abrir Grafana para crear dashboard
  openGrafanaToCreateDashboard() {
    const newDashboardUrl = `${this.grafanaUrl}/dashboard/new`;
    window.open(newDashboardUrl, '_blank');
    console.log(`
    Pasos para crear un dashboard en Grafana:
    
    1. Se abrirá Grafana en una nueva pestaña
    2. Haz clic en "Add visualization"
    3. Selecciona tu fuente de datos de Prometheus
    4. Agrega una consulta, por ejemplo: up
    5. Guarda el dashboard con un nombre
    6. Copia el UID del dashboard de la URL
    7. Pégalo en grafanaDashboardId en este componente
    `);
  }

  // Obtener datos de Prometheus
  fetchPrometheusData() {
    this.metricsLoading = true;
    this.metricsError = null;

    const requests = this.prometheusQueries.map(metric => 
      this.queryPrometheus(metric.query).pipe(
        catchError(error => {
          console.error(`Error fetching ${metric.name}:`, error);
          return of(null);
        })
      )
    );

    // Ejecutar todas las consultas en paralelo
    Promise.all(requests.map(req => req.toPromise()))
      .then(results => {
        this.prometheusData = this.prometheusQueries.map((metric, index) => ({
          name: metric.name,
          color: metric.color,
          value: this.extractMetricValue(results[index] ?? null),
          timestamp: new Date()
        }));
        this.metricsLoading = false;
      })
      .catch(error => {
        this.metricsError = 'Error al cargar métricas de Prometheus';
        this.metricsLoading = false;
        console.error('Prometheus fetch error:', error);
      });
  }

  // Consultar Prometheus API
  private queryPrometheus(query: string) {
    const url = `${this.prometheusUrl}/api/v1/query`;
    const params = { query };
    
    return this.http.get<PrometheusResponse>(url, { 
      params,
      headers: this.getPrometheusHeaders()
    });
  }

  // Headers para Prometheus (si requiere autenticación)
  private getPrometheusHeaders(): HttpHeaders {
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    
    // Agregar autenticación si es necesario
    // headers = headers.set('Authorization', 'Bearer your-token');
    
    return headers;
  }

  // Extraer valor de la respuesta de Prometheus
  private extractMetricValue(response: PrometheusResponse | null): number {
    if (!response || !response.data || !response.data.result || response.data.result.length === 0) {
      return 0;
    }
    
    const result = response.data.result[0];
    return parseFloat(result.value[1]) || 0;
  }

  // Iniciar la obtención periódica de datos
  startPrometheusDataFetch() {
    // Obtener datos inmediatamente
    this.fetchPrometheusData();
    
    // Actualizar cada 30 segundos
    this.dataSubscription = interval(30000)
      .pipe(
        switchMap(() => {
          this.fetchPrometheusData();
          return of(null);
        })
      )
      .subscribe();
  }

  // Mostrar/ocultar panel de Grafana
  toggleGrafanaPanel() {
    this.showGrafanaPanel = !this.showGrafanaPanel;
    if (this.showGrafanaPanel) {
      // Reconstruir URL cuando se muestra
      this.setupGrafanaEmbed();
      this.grafanaLoaded = false;
      this.grafanaError = false;
    }
  }

  // Manejar carga del iframe
  onGrafanaLoad() {
    this.grafanaLoaded = true;
    this.grafanaError = false;
  }

  // Manejar error del iframe
  onGrafanaError() {
    this.grafanaError = true;
    this.grafanaLoaded = false;
  }

  // Configurar Grafana para permitir embedding
  configureGrafanaEmbedding() {
    // Instrucciones para configurar Grafana
    console.log(`
    Para permitir embedding en Grafana, configura en grafana.ini:
    
    [security]
    allow_embedding = true
    cookie_samesite = none
    cookie_secure = false
    
    [auth.anonymous]
    enabled = true
    org_role = Viewer
    
    Luego reinicia Grafana
    `);
  }

  // Refrescar datos manualmente
  refreshData() {
    this.fetchPrometheusData();
  }

  // Abrir Grafana en nueva pestaña
  openGrafanaFullscreen() {
    const fullUrl = `${this.grafanaUrl}/d/${this.grafanaDashboardId}/dashboard-name`;
    window.open(fullUrl, '_blank');
  }

  // Formatear valores para mostrar
  formatMetricValue(value: number, suffix: string = '%'): string {
    return `${value.toFixed(2)}${suffix}`;
  }

  // Obtener color de estado basado en el valor
  getStatusColor(value: number): string {
    if (value < 50) return '#4caf50'; // Verde
    if (value < 80) return '#ff9800'; // Naranja
    return '#f44336'; // Rojo
  }
}