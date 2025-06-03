import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface Experto {
  id: number;
  nombre: string;
  especialidad: string;
  experiencia: string;
  calificacion: number;
  descripcion: string;
  imagen: string;
}

interface Asesoria {
  id: number;
  expertoId: number;
  expertoNombre: string;
  especialidad: string;
  fechaSolicitud: Date;
  estado: 'pendiente' | 'aceptada' | 'rechazada';
  contacto?: {
    email: string;
    telefono: string;
  };
}

@Component({
  selector: 'app-asesorias',
  imports: [CommonModule],
  templateUrl: './asesorias.component.html',
  styleUrl: './asesorias.component.css'
})
export class AsesoriasComponent {
  seccionActiva: 'disponibles' | 'mis-asesorias' = 'disponibles';
  
  expertosDisponibles: Experto[] = [
    {
      id: 1,
      nombre: 'Dr. María González',
      especialidad: 'Cultivos de Cereales',
      experiencia: '15 años de experiencia',
      calificacion: 4.8,
      descripcion: 'Especialista en optimización de cultivos de maíz, trigo y arroz. Experta en técnicas de riego y fertilización sostenible.',
      imagen: 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=150&h=150&fit=crop&crop=face'
    },
    {
      id: 2,
      nombre: 'Ing. Carlos Rodríguez',
      especialidad: 'Ganadería Sostenible',
      experiencia: '12 años de experiencia',
      calificacion: 4.9,
      descripcion: 'Consultor en manejo integral de ganado bovino y sistemas de pastoreo rotacional. Especialista en nutrición animal.',
      imagen: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face'
    }
  ];

  misAsesorias: Asesoria[] = [];
  
  solicitudCounter = 1;

  cambiarSeccion(seccion: 'disponibles' | 'mis-asesorias') {
    this.seccionActiva = seccion;
  }

  solicitarAsesoria(experto: Experto) {
    const nuevaAsesoria: Asesoria = {
      id: this.solicitudCounter++,
      expertoId: experto.id,
      expertoNombre: experto.nombre,
      especialidad: experto.especialidad,
      fechaSolicitud: new Date(),
      estado: 'pendiente'
    };

    this.misAsesorias.push(nuevaAsesoria);
    
    // Simular respuesta del experto después de 3 segundos
    setTimeout(() => {
      this.simularRespuestaExperto(nuevaAsesoria.id);
    }, 3000);

    // Cambiar a la sección de mis asesorías
    this.seccionActiva = 'mis-asesorias';
  }

  private simularRespuestaExperto(asesoriaId: number) {
    const asesoria = this.misAsesorias.find(a => a.id === asesoriaId);
    if (asesoria) {
      // 80% probabilidad de aceptar, 20% de rechazar
      const esAceptada = Math.random() > 0.2;
      
      if (esAceptada) {
        asesoria.estado = 'aceptada';
        asesoria.contacto = {
          email: `${asesoria.expertoNombre.toLowerCase().replace(/\s+/g, '').replace('dr.', '').replace('ing.', '')}@agroconsulta.com`,
          telefono: `+57 ${Math.floor(300 + Math.random() * 20)} ${Math.floor(100 + Math.random() * 899)} ${Math.floor(1000 + Math.random() * 8999)}`
        };
      } else {
        asesoria.estado = 'rechazada';
      }
    }
  }

  getEstadoClase(estado: string): string {
    switch(estado) {
      case 'pendiente': return 'estado-pendiente';
      case 'aceptada': return 'estado-aceptada';
      case 'rechazada': return 'estado-rechazada';
      default: return '';
    }
  }

  getEstadoTexto(estado: string): string {
    switch(estado) {
      case 'pendiente': return 'Pendiente';
      case 'aceptada': return 'Aceptada';
      case 'rechazada': return 'Rechazada';
      default: return estado;
    }
  }
}