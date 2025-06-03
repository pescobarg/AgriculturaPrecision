import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface SolicitudAsesoria {
  id: number;
  nombreUsuario: string;
  correo: string;
  urgencia: 'baja' | 'media' | 'alta';
}

export interface AsesoriaAceptada {
  id: number;
  nombreUsuario: string;
  correo: string;
  estado: 'programada' | 'en-curso' | 'completada';
}

@Component({
  selector: 'app-citas',
  imports: [CommonModule],
  templateUrl: './citas.component.html',
  styleUrl: './citas.component.css'
})
export class CitasComponent {
  activeTab: 'solicitudes' | 'asesorias' = 'solicitudes';

  solicitudesPendientes: SolicitudAsesoria[] = [
    {
      id: 1,
      nombreUsuario: 'Agi',
      correo: 'agricultor@gmail.com',
      urgencia: 'alta'
    },
    {
      id: 2,
      nombreUsuario: 'María Sanchez',
      correo: 'maria.sanchez@email.com',
      urgencia: 'alta'
    },
    {
      id: 3,
      nombreUsuario: 'Carlos Parra',
      correo: 'carlos.r@farm.com',
      urgencia: 'media'
    },
    {
      id: 4,
      nombreUsuario: 'Ana Martínez',
      correo: 'ana.martinez@agro.com',
      urgencia: 'baja'
    }
  ];

  misAsesorias: AsesoriaAceptada[] = [
    {
      id: 101,
      nombreUsuario: 'Pedro Sánchez',
      correo: 'pedro.sanchez@campo.com',
      estado: 'programada'
    }
  ];

  setActiveTab(tab: 'solicitudes' | 'asesorias') {
    this.activeTab = tab;
  }

  aceptarSolicitud(solicitud: SolicitudAsesoria) {
    // Crear nueva asesoría
    const nuevaAsesoria: AsesoriaAceptada = {
      id: Date.now(), // ID temporal
      nombreUsuario: solicitud.nombreUsuario,
      correo: solicitud.correo,
      estado: 'programada'
    };

    this.misAsesorias.push(nuevaAsesoria);
    
    // Remover de solicitudes pendientes
    this.solicitudesPendientes = this.solicitudesPendientes.filter(s => s.id !== solicitud.id);
    
    alert(`Solicitud de ${solicitud.nombreUsuario} aceptada y programada exitosamente.`);
  }

  rechazarSolicitud(solicitud: SolicitudAsesoria) {
    if (confirm(`¿Estás seguro de que quieres rechazar la solicitud de ${solicitud.nombreUsuario}?`)) {
      this.solicitudesPendientes = this.solicitudesPendientes.filter(s => s.id !== solicitud.id);
      alert(`Solicitud de ${solicitud.nombreUsuario} rechazada.`);
    }
  }

  getUrgenciaClass(urgencia: string): string {
    switch(urgencia) {
      case 'alta': return 'urgencia-alta';
      case 'media': return 'urgencia-media';
      case 'baja': return 'urgencia-baja';
      default: return '';
    }
  }

  getEstadoClass(estado: string): string {
    switch(estado) {
      case 'programada': return 'estado-programada';
      case 'en-curso': return 'estado-en-curso';
      case 'completada': return 'estado-completada';
      default: return '';
    }
  }

  formatearFecha(fecha: Date): string {
    return fecha.toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }
}