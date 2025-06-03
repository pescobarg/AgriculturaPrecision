// carrito.component.ts
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

interface ProductoCarrito {
  id: number;
  nombre: string;
  descripcion: string;
  categoria: string;
  proveedor: string;
  etiquetas: string[];
  precio: number;
  unidad: string;
  cantidad: number;
  imagen: string;
}

@Component({
  selector: 'app-carrito',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './carrito.component.html',
  styleUrls: ['./carrito.component.css']
})
export class CarritoComponent {
  productos: ProductoCarrito[] = [
    {
      id: 1,
      nombre: 'Semillas de Maíz Premium',
      descripcion: 'Semillas de maíz de alta calidad, resistentes a plagas',
      categoria: 'Semillas',
      proveedor: 'AgroSemillas S.A.',
      etiquetas: ['Orgánico', 'No GMO'],
      precio: 45.99,
      unidad: 'kg',
      cantidad: 2,
      imagen: 'assets/imagen_producto.jpg'
    },
    {
      id: 2,
      nombre: 'Fertilizante Orgánico',
      descripcion: 'Fertilizante 100% orgánico para cultivos sostenibles',
      categoria: 'Fertilizantes',
      proveedor: 'EcoFertil',
      etiquetas: ['Orgánico'],
      precio: 32.50,
      unidad: 'kg',
      cantidad: 3,
      imagen: 'assets/imagen_producto.jpg'
    },
    {
      id: 3,
      nombre: 'Semillas de Tomate Cherry',
      descripcion: 'Variedad de tomate cherry de excelente sabor',
      categoria: 'Semillas',
      proveedor: 'AgroSemillas S.A.',
      etiquetas: ['Orgánico'],
      precio: 12.99,
      unidad: 'sobre',
      cantidad: 5,
      imagen: 'assets/imagen_producto.jpg'
    },
    {
      id: 4,
      nombre: 'Abono Compost Premium',
      descripcion: 'Abono orgánico enriquecido con nutrientes esenciales',
      categoria: 'Fertilizantes',
      proveedor: 'TierraVerde',
      etiquetas: ['Orgánico', 'Premium'],
      precio: 28.75,
      unidad: 'kg',
      cantidad: 1,
      imagen: 'assets/imagen_producto.jpg'
    }
  ];

  constructor(private router: Router) {}

  aumentarCantidad(producto: ProductoCarrito): void {
    producto.cantidad++;
  }

  disminuirCantidad(producto: ProductoCarrito): void {
    if (producto.cantidad > 1) {
      producto.cantidad--;
    }
  }

  eliminarProducto(id: number): void {
    this.productos = this.productos.filter(producto => producto.id !== id);
  }

  calcularSubtotal(producto: ProductoCarrito): number {
    return producto.precio * producto.cantidad;
  }

  calcularTotal(): number {
    return this.productos.reduce((total, producto) => total + this.calcularSubtotal(producto), 0);
  }

  procederAlPago(): void {
    this.router.navigate(['/pedidos']);
  }
}
