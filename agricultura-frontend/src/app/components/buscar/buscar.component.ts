import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

interface Producto {
  id: number;
  nombre: string;
  categoria: string;
  precio: number;
  unidad: string;
  descripcion: string;
  imagen: string;
  proveedor: string;
  disponible: boolean;
  origen: string;
  certificacion: string[];
}

@Component({
  selector: 'app-buscar',
  imports: [CommonModule, FormsModule],
  templateUrl: './buscar.component.html',
  styleUrl: './buscar.component.css'
})
export class BuscarComponent {
  // Filtros
  filtroCategoria: string = '';
  filtroProveedor: string = '';
  filtroPrecioMin: number = 0;
  filtroPrecioMax: number = 1000;
  filtroOrigen: string = '';
  filtroCertificacion: string = '';
  filtroDisponible: boolean = false;
  terminoBusqueda: string = '';

  // Datos de productos (simulados)
  productos: Producto[] = [
    {
      id: 1,
      nombre: 'Semillas de Maíz Premium',
      categoria: 'Semillas',
      precio: 45.99,
      unidad: 'kg',
      descripcion: 'Semillas de maíz de alta calidad, resistentes a plagas',
      imagen: 'assets/imagen_producto.jpg',
      proveedor: 'AgroSemillas S.A.',
      disponible: true,
      origen: 'Nacional',
      certificacion: ['Orgánico', 'No GMO']
    },
    {
      id: 2,
      nombre: 'Fertilizante Orgánico',
      categoria: 'Fertilizantes',
      precio: 32.50,
      unidad: 'kg',
      descripcion: 'Fertilizante 100% orgánico para cultivos sostenibles',
      imagen: 'assets/imagen_producto.jpg',
      proveedor: 'EcoFertil',
      disponible: true,
      origen: 'Importado',
      certificacion: ['Orgánico']
    },
    {
      id: 3,
      nombre: 'Pesticida Natural',
      categoria: 'Pesticidas',
      precio: 28.75,
      unidad: 'litro',
      descripcion: 'Control de plagas con ingredientes naturales',
      imagen: 'assets/imagen_producto.jpg',
      proveedor: 'BioControl',
      disponible: false,
      origen: 'Nacional',
      certificacion: ['Ecológico']
    },
    {
      id: 4,
      nombre: 'Semillas de Tomate Cherry',
      categoria: 'Semillas',
      precio: 12.99,
      unidad: 'sobre',
      descripcion: 'Variedad de tomate cherry de excelente sabor',
      imagen: 'assets/imagen_producto.jpg',
      proveedor: 'AgroSemillas S.A.',
      disponible: true,
      origen: 'Nacional',
      certificacion: ['Orgánico']
    },
    {
      id: 5,
      nombre: 'Abono Foliar',
      categoria: 'Fertilizantes',
      precio: 18.20,
      unidad: 'litro',
      descripcion: 'Nutrición foliar completa para mayor productividad',
      imagen: 'assets/imagen_producto.jpg',
      proveedor: 'NutriPlant',
      disponible: true,
      origen: 'Importado',
      certificacion: ['Certificado']
    },
    {
      id: 6,
      nombre: 'Herbicida Selectivo',
      categoria: 'Herbicidas',
      precio: 65.80,
      unidad: 'litro',
      descripcion: 'Control selectivo de malezas sin dañar el cultivo',
      imagen: 'assets/imagen_producto.jpg',
      proveedor: 'AgroQuímica',
      disponible: true,
      origen: 'Importado',
      certificacion: ['Registrado SENASA']
    }
  ];

  // Listas para filtros
  categorias: string[] = [];
  proveedores: string[] = [];
  origenes: string[] = [];
  certificaciones: string[] = [];

  constructor(private router: Router) {
    this.inicializarFiltros();
  }

  inicializarFiltros() {
    this.categorias = [...new Set(this.productos.map(p => p.categoria))];
    this.proveedores = [...new Set(this.productos.map(p => p.proveedor))];
    this.origenes = [...new Set(this.productos.map(p => p.origen))];
    
    const todasCertificaciones = this.productos.flatMap(p => p.certificacion);
    this.certificaciones = [...new Set(todasCertificaciones)];
  }

  get productosFiltrados(): Producto[] {
    return this.productos.filter(producto => {
      const cumpleTermino = !this.terminoBusqueda || 
        producto.nombre.toLowerCase().includes(this.terminoBusqueda.toLowerCase()) ||
        producto.descripcion.toLowerCase().includes(this.terminoBusqueda.toLowerCase());
      
      const cumpleCategoria = !this.filtroCategoria || producto.categoria === this.filtroCategoria;
      const cumpleProveedor = !this.filtroProveedor || producto.proveedor === this.filtroProveedor;
      const cumplePrecio = producto.precio >= this.filtroPrecioMin && producto.precio <= this.filtroPrecioMax;
      const cumpleOrigen = !this.filtroOrigen || producto.origen === this.filtroOrigen;
      const cumpleCertificacion = !this.filtroCertificacion || 
        producto.certificacion.includes(this.filtroCertificacion);
      const cumpleDisponible = !this.filtroDisponible || producto.disponible;

      return cumpleTermino && cumpleCategoria && cumpleProveedor && cumplePrecio && 
             cumpleOrigen && cumpleCertificacion && cumpleDisponible;
    });
  }

  limpiarFiltros() {
    this.filtroCategoria = '';
    this.filtroProveedor = '';
    this.filtroPrecioMin = 0;
    this.filtroPrecioMax = 1000;
    this.filtroOrigen = '';
    this.filtroCertificacion = '';
    this.filtroDisponible = false;
    this.terminoBusqueda = '';
  }

  verDetalles(producto: Producto) {
    this.router.navigate(['/detalles-producto']);
  }

  irAlCarrito() {
    this.router.navigate(['/carrito']);
  }
  verMisPedidos() {
    this.router.navigate(['/pedidos']);
  }
}