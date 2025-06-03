import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

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
  selector: 'app-productos',
  imports: [CommonModule, FormsModule],
  templateUrl: './productos.component.html',
  styleUrl: './productos.component.css'
})
export class ProductosComponent {
  productos: ProductoDetalle[] = [
    {
      id: 1,
      nombre: 'Tomate Cherry Orgánico',
      categoria: 'Verduras',
      precio: 12500,
      unidad: 'kg',
      descripcion: 'Tomates cherry frescos y orgánicos',
      descripcionCompleta: 'Tomates cherry cultivados de manera orgánica, perfectos para ensaladas y platos gourmet. Sabor dulce y textura firme.',
      imagen: 'https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=300&h=200&fit=crop',
      proveedor: 'Finca Verde S.A.S',
      disponible: true,
      stock: 25,
      origen: 'Colombia - Cundinamarca',
      certificacion: ['Orgánico', 'HACCP'],
      caracteristicas: ['Sin pesticidas', 'Cultivo sostenible', 'Rico en licopeno'],
      usoRecomendado: ['Ensaladas', 'Salsas', 'Snacks'],
      composicion: 'Tomate 100% natural',
      presentacion: 'Bandeja de 500g',
      fechaVencimiento: '2025-06-15',
      codigoProducto: 'TCO-001',
      pesoNeto: '500g',
      temperaturaAlmacenamiento: '8-12°C',
      instrucciones: ['Lavar antes de consumir', 'Conservar en refrigeración'],
      precauciones: ['Mantener alejado de la luz directa'],
      beneficios: ['Rico en vitamina C', 'Antioxidantes naturales', 'Bajo en calorías']
    },
    {
      id: 2,
      nombre: 'Aguacate Hass Premium',
      categoria: 'Frutas',
      precio: 8900,
      unidad: 'kg',
      descripcion: 'Aguacates Hass de exportación',
      descripcionCompleta: 'Aguacates Hass de primera calidad, ideales para consumo directo o preparación de guacamole. Textura cremosa y sabor excepcional.',
      imagen: 'https://images.unsplash.com/photo-1523049673857-eb18f1d7b578?w=300&h=200&fit=crop',
      proveedor: 'Aguacates del Huila',
      disponible: true,
      stock: 40,
      origen: 'Colombia - Huila',
      certificacion: ['Global GAP', 'Rainforest Alliance'],
      caracteristicas: ['Maduración perfecta', 'Sin manchas', 'Tamaño uniforme'],
      usoRecomendado: ['Guacamole', 'Ensaladas', 'Tostadas'],
      composicion: 'Aguacate 100% natural',
      presentacion: 'Malla de 1kg',
      fechaVencimiento: '2025-06-20',
      codigoProducto: 'AHA-002',
      pesoNeto: '1kg',
      temperaturaAlmacenamiento: '12-15°C',
      instrucciones: ['Verificar madurez antes de consumir', 'Conservar a temperatura ambiente hasta madurar'],
      precauciones: ['No refrigerar hasta estar maduro'],
      beneficios: ['Rico en grasas saludables', 'Vitamina E', 'Fibra natural']
    }
  ];

  // Estado del formulario
  mostrarFormulario = false;
  editando = false;
  productoSeleccionado: ProductoDetalle | null = null;
  mostrarDetalle = false;

  // Producto para el formulario
  productoForm: ProductoDetalle = this.getEmptyProduct();

  // Campos de arrays temporales para el formulario
  nuevaCertificacion = '';
  nuevaCaracteristica = '';
  nuevoUsoRecomendado = '';
  nuevaInstruccion = '';
  nuevaPrecaucion = '';
  nuevoBeneficio = '';

  getEmptyProduct(): ProductoDetalle {
    return {
      id: 0,
      nombre: '',
      categoria: '',
      precio: 0,
      unidad: '',
      descripcion: '',
      descripcionCompleta: '',
      imagen: '',
      proveedor: '',
      disponible: true,
      stock: 0,
      origen: '',
      certificacion: [],
      caracteristicas: [],
      usoRecomendado: [],
      composicion: '',
      presentacion: '',
      fechaVencimiento: '',
      codigoProducto: '',
      pesoNeto: '',
      temperaturaAlmacenamiento: '',
      instrucciones: [],
      precauciones: [],
      beneficios: []
    };
  }

  // CRUD Operations
  crearProducto() {
    this.editando = false;
    this.mostrarFormulario = true;
    this.productoForm = this.getEmptyProduct();
    this.limpiarCamposTemporales();
  }

  editarProducto(producto: ProductoDetalle) {
    this.editando = true;
    this.mostrarFormulario = true;
    this.productoForm = { ...producto };
    this.limpiarCamposTemporales();
  }

  guardarProducto() {
    if (this.validarFormulario()) {
      if (this.editando) {
        const index = this.productos.findIndex(p => p.id === this.productoForm.id);
        if (index !== -1) {
          this.productos[index] = { ...this.productoForm };
        }
      } else {
        this.productoForm.id = this.getNextId();
        this.productos.push({ ...this.productoForm });
      }
      this.cancelarFormulario();
    }
  }

  eliminarProducto(id: number) {
    if (confirm('¿Estás seguro de que deseas eliminar este producto?')) {
      this.productos = this.productos.filter(p => p.id !== id);
    }
  }

  verDetalle(producto: ProductoDetalle) {
    this.productoSeleccionado = producto;
    this.mostrarDetalle = true;
  }

  cancelarFormulario() {
    this.mostrarFormulario = false;
    this.editando = false;
    this.productoForm = this.getEmptyProduct();
    this.limpiarCamposTemporales();
  }

  cerrarDetalle() {
    this.mostrarDetalle = false;
    this.productoSeleccionado = null;
  }

  // Funciones auxiliares
  getNextId(): number {
    return Math.max(...this.productos.map(p => p.id), 0) + 1;
  }

  validarFormulario(): boolean {
    return this.productoForm.nombre.trim() !== '' &&
           this.productoForm.categoria.trim() !== '' &&
           this.productoForm.precio > 0;
  }

  limpiarCamposTemporales() {
    this.nuevaCertificacion = '';
    this.nuevaCaracteristica = '';
    this.nuevoUsoRecomendado = '';
    this.nuevaInstruccion = '';
    this.nuevaPrecaucion = '';
    this.nuevoBeneficio = '';
  }

  // Funciones para manejar arrays
  agregarCertificacion() {
    if (this.nuevaCertificacion.trim()) {
      this.productoForm.certificacion.push(this.nuevaCertificacion.trim());
      this.nuevaCertificacion = '';
    }
  }

  eliminarCertificacion(index: number) {
    this.productoForm.certificacion.splice(index, 1);
  }

  agregarCaracteristica() {
    if (this.nuevaCaracteristica.trim()) {
      this.productoForm.caracteristicas.push(this.nuevaCaracteristica.trim());
      this.nuevaCaracteristica = '';
    }
  }

  eliminarCaracteristica(index: number) {
    this.productoForm.caracteristicas.splice(index, 1);
  }

  agregarUsoRecomendado() {
    if (this.nuevoUsoRecomendado.trim()) {
      this.productoForm.usoRecomendado.push(this.nuevoUsoRecomendado.trim());
      this.nuevoUsoRecomendado = '';
    }
  }

  eliminarUsoRecomendado(index: number) {
    this.productoForm.usoRecomendado.splice(index, 1);
  }

  agregarInstruccion() {
    if (this.nuevaInstruccion.trim()) {
      this.productoForm.instrucciones.push(this.nuevaInstruccion.trim());
      this.nuevaInstruccion = '';
    }
  }

  eliminarInstruccion(index: number) {
    this.productoForm.instrucciones.splice(index, 1);
  }

  agregarPrecaucion() {
    if (this.nuevaPrecaucion.trim()) {
      this.productoForm.precauciones.push(this.nuevaPrecaucion.trim());
      this.nuevaPrecaucion = '';
    }
  }

  eliminarPrecaucion(index: number) {
    this.productoForm.precauciones.splice(index, 1);
  }

  agregarBeneficio() {
    if (this.nuevoBeneficio.trim()) {
      this.productoForm.beneficios.push(this.nuevoBeneficio.trim());
      this.nuevoBeneficio = '';
    }
  }

  eliminarBeneficio(index: number) {
    this.productoForm.beneficios.splice(index, 1);
  }
}