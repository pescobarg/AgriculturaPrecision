import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

interface ProductoDetalle {
  id: number;
  nombre: string;
  categoria: string;
  precio: number;
  unidad: string;
  descripcion: string;
  descripcionCompleta: string;
  imagen: string;
  proveedor: string;
  disponible: boolean;
  stock: number;
  origen: string;
  certificacion: string[];
  caracteristicas: string[];
  usoRecomendado: string[];
  composicion: string;
  presentacion: string;
  fechaVencimiento: string;
  codigoProducto: string;
  pesoNeto: string;
  temperaturaAlmacenamiento: string;
  instrucciones: string[];
  precauciones: string[];
  beneficios: string[];
}

@Component({
  selector: 'app-detalles-producto',
  imports: [CommonModule, FormsModule],
  templateUrl: './detalles-producto.component.html',
  styleUrl: './detalles-producto.component.css'
})
export class DetallesProductoComponent {
  cantidad: number = 1;
  tabActiva: string = 'descripcion';
  
  // Producto de ejemplo (normalmente vendría de un servicio)
  producto: ProductoDetalle = {
    id: 1,
    nombre: 'Semillas de Maíz Premium',
    categoria: 'Semillas',
    precio: 45.99,
    unidad: 'kg',
    descripcion: 'Semillas de maíz de alta calidad, resistentes a plagas',
    descripcionCompleta: 'Nuestras semillas de maíz premium son el resultado de años de investigación y desarrollo. Estas semillas híbridas ofrecen una excelente resistencia a plagas y enfermedades, garantizando una cosecha abundante y de alta calidad. Ideales para agricultores que buscan maximizar su rendimiento con productos sostenibles.',
    imagen: 'assets/imagen_producto.jpg',
    proveedor: 'AgroSemillas S.A.',
    disponible: true,
    stock: 150,
    origen: 'Nacional',
    certificacion: ['Orgánico', 'No GMO', 'Certificado INTA'],
    caracteristicas: [
      'Alto rendimiento por hectárea',
      'Resistencia a sequías moderadas',
      'Período de cosecha: 120-130 días',
      'Altura promedio: 2.5-3.0 metros',
      'Mazorcas de 18-20 cm de longitud',
      'Granos de color amarillo intenso'
    ],
    usoRecomendado: [
      'Cultivo en suelos bien drenados',
      'Siembra en primavera (septiembre-noviembre)',
      'Distancia entre plantas: 25-30 cm',
      'Profundidad de siembra: 3-4 cm',
      'Riego constante durante floración',
      'Fertilización cada 30 días'
    ],
    composicion: 'Semillas híbridas seleccionadas, tratamiento fungicida natural',
    presentacion: 'Bolsa de 1 kg, empaque al vacío',
    fechaVencimiento: '12/2025',
    codigoProducto: 'SM-MAIZ-001',
    pesoNeto: '1000g',
    temperaturaAlmacenamiento: '5°C - 25°C, lugar seco',
    instrucciones: [
      'Preparar el suelo con arado profundo',
      'Realizar surcos de 3-4 cm de profundidad',
      'Colocar 2-3 semillas por hoyo',
      'Cubrir con tierra suelta',
      'Regar inmediatamente después de la siembra',
      'Mantener humedad constante durante germinación'
    ],
    precauciones: [
      'Mantener fuera del alcance de niños',
      'No consumir las semillas tratadas',
      'Usar guantes durante la manipulación',
      'Almacenar en lugar fresco y seco',
      'Evitar exposición directa al sol'
    ],
    beneficios: [
      'Incrementa el rendimiento hasta un 30%',
      'Mayor resistencia a plagas comunes',
      'Adaptación a diferentes tipos de suelo',
      'Mazorcas uniformes y de gran tamaño',
      'Excelente calidad nutricional del grano'
    ]
  };

  constructor(private router: Router) {}

  seleccionarTab(tab: string) {
    this.tabActiva = tab;
  }

  aumentarCantidad() {
    if (this.cantidad < this.producto.stock) {
      this.cantidad++;
    }
  }

  disminuirCantidad() {
    if (this.cantidad > 1) {
      this.cantidad--;
    }
  }

  agregarAlCarrito() {
    // Aquí implementarías la lógica para agregar al carrito
    console.log(`Agregando ${this.cantidad} unidades de ${this.producto.nombre} al carrito`);
    
    // Mostrar mensaje de confirmación
    alert(`¡${this.producto.nombre} agregado al carrito!\nCantidad: ${this.cantidad} ${this.producto.unidad}`);
  }

  irAlCarrito() {
    this.router.navigate(['/carrito']);
  }

  volverAlCatalogo() {
    this.router.navigate(['/buscar']);
  }

  get precioTotal(): number {
    return this.producto.precio * this.cantidad;
  }

  verMisPedidos() {
    this.router.navigate(['/pedidos']);
  }

}