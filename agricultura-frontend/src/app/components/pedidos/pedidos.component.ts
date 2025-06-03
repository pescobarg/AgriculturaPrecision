import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

type EstadoPedido = 'en_almacen' | 'en_curso' | 'fallido' | 'para_recoger' | 'recogido';

interface ProductoPedido {
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

interface Pedido {
  id: string;
  fecha: Date;
  estado: EstadoPedido;
  productos: ProductoPedido[];
  total: number;
  direccionEntrega: string;
  metodoPago: string;
}

@Component({
  selector: 'app-pedidos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pedidos.component.html',
  styleUrls: ['./pedidos.component.css']
})
export class PedidosComponent {
  pedidos: Pedido[] = [
    {
      id: 'PED-2025-001',
      fecha: new Date('2025-06-01'),
      estado: 'en_curso',
      direccionEntrega: 'Calle 85 #15-32, Bogotá, Colombia',
      metodoPago: 'Tarjeta de Crédito',
      productos: [
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
      ],
      total: 283.18
    },
    {
      id: 'PED-2025-002',
      fecha: new Date('2025-05-28'),
      estado: 'para_recoger',
      direccionEntrega: 'Centro de Distribución AgroVerde - Zona Industrial',
      metodoPago: 'Transferencia Bancaria',
      productos: [
        {
          id: 5,
          nombre: 'Insecticida Natural',
          descripcion: 'Control de plagas 100% natural',
          categoria: 'Pesticidas',
          proveedor: 'BioControl',
          etiquetas: ['Orgánico', 'Natural'],
          precio: 24.99,
          unidad: 'litro',
          cantidad: 2,
          imagen: 'assets/imagen_producto.jpg'
        }
      ],
      total: 49.98
    },
    {
      id: 'PED-2025-003',
      fecha: new Date('2025-05-25'),
      estado: 'recogido',
      direccionEntrega: 'Calle 72 #45-18, Bogotá, Colombia',
      metodoPago: 'Efectivo',
      productos: [
        {
          id: 6,
          nombre: 'Kit de Herramientas Básicas',
          descripcion: 'Set completo para jardinería',
          categoria: 'Herramientas',
          proveedor: 'ToolsAgro',
          etiquetas: ['Premium', 'Durable'],
          precio: 89.99,
          unidad: 'set',
          cantidad: 1,
          imagen: 'assets/imagen_producto.jpg'
        }
      ],
      total: 89.99
    },
    {
      id: 'PED-2025-004',
      fecha: new Date('2025-05-20'),
      estado: 'fallido',
      direccionEntrega: 'Carrera 15 #25-10, Bogotá, Colombia',
      metodoPago: 'Tarjeta de Débito',
      productos: [
        {
          id: 7,
          nombre: 'Sustrato Premium',
          descripcion: 'Tierra preparada para siembra',
          categoria: 'Sustratos',
          proveedor: 'TierraRich',
          etiquetas: ['Premium'],
          precio: 15.50,
          unidad: 'kg',
          cantidad: 5,
          imagen: 'assets/imagen_producto.jpg'
        }
      ],
      total: 77.50
    },
    {
      id: 'PED-2025-005',
      fecha: new Date('2025-05-15'),
      estado: 'en_almacen',
      direccionEntrega: 'Avenida 68 #12-34, Bogotá, Colombia',
      metodoPago: 'PayPal',
      productos: [
        {
          id: 8,
          nombre: 'Regadera Automática',
          descripcion: 'Sistema de riego por goteo',
          categoria: 'Riego',
          proveedor: 'AquaTech',
          etiquetas: ['Tecnología', 'Ahorro'],
          precio: 156.00,
          unidad: 'unidad',
          cantidad: 1,
          imagen: 'assets/imagen_producto.jpg'
        }
      ],
      total: 156.00
    }
  ];

  constructor() {}

  getEstadoConfig(estado: EstadoPedido) {
    const configs = {
      'en_almacen': {
        texto: 'En Almacén',
        icono: '📦',
        clase: 'estado-almacen',
        descripcion: 'Tu pedido está siendo preparado en nuestro almacén'
      },
      'en_curso': {
        texto: 'En Curso',
        icono: '🚛',
        clase: 'estado-curso',
        descripcion: 'Tu pedido está en camino'
      },
      'fallido': {
        texto: 'Fallido',
        icono: '❌',
        clase: 'estado-fallido',
        descripcion: 'Hubo un problema con la entrega'
      },
      'para_recoger': {
        texto: 'Para Recoger',
        icono: '📍',
        clase: 'estado-recoger',
        descripcion: 'Tu pedido está listo para recoger'
      },
      'recogido': {
        texto: 'Recogido',
        icono: '✅',
        clase: 'estado-recogido',
        descripcion: 'Pedido entregado exitosamente'
      }
    };
    return configs[estado];
  }

  calcularSubtotal(producto: ProductoPedido): number {
    return producto.precio * producto.cantidad;
  }

  formatearFecha(fecha: Date): string {
    return fecha.toLocaleDateString('es-CO', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }





  descargarFactura(pedido: Pedido): void {
    // Crear el contenido HTML de la factura
    const facturaHTML = this.generarFacturaHTML(pedido);
    
    // Crear un elemento temporal para la factura
    const facturaElement = document.createElement('div');
    facturaElement.innerHTML = facturaHTML;
    facturaElement.style.padding = '20px';
    facturaElement.style.fontFamily = 'Arial, sans-serif';
    facturaElement.style.backgroundColor = 'white';
    facturaElement.style.color = 'black';
    
    // Abrir ventana de impresión
    const ventanaImpresion = window.open('', '_blank');
    if (ventanaImpresion) {
      ventanaImpresion.document.write(`
        <!DOCTYPE html>
        <html>
        <head>
          <title>Factura ${pedido.id}</title>
          <style>
            body { font-family: Arial, sans-serif; margin: 20px; }
            .factura-header { text-align: center; margin-bottom: 30px; border-bottom: 2px solid #1b4332; padding-bottom: 20px; }
            .logo { font-size: 24px; font-weight: bold; color: #1b4332; margin-bottom: 10px; }
            .info-empresa { color: #666; }
            .factura-info { display: flex; justify-content: space-between; margin-bottom: 30px; }
            .info-block { flex: 1; }
            .info-title { font-weight: bold; color: #1b4332; margin-bottom: 10px; }
            .productos-table { width: 100%; border-collapse: collapse; margin-bottom: 30px; }
            .productos-table th, .productos-table td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
            .productos-table th { background-color: #1b4332; color: white; }
            .productos-table tr:nth-child(even) { background-color: #f8f9fa; }
            .total-section { text-align: right; margin-top: 20px; }
            .total-final { font-size: 18px; font-weight: bold; color: #1b4332; border-top: 2px solid #1b4332; padding-top: 10px; }
            .estado-badge { display: inline-block; padding: 5px 10px; border-radius: 15px; font-size: 12px; font-weight: bold; }
            .estado-${pedido.estado} { background: #d4edda; color: #155724; }
            .footer { margin-top: 40px; text-align: center; color: #666; font-size: 12px; }
          </style>
        </head>
        <body>
          ${facturaHTML}
          <script>
            window.onload = function() {
              window.print();
            }
          </script>
        </body>
        </html>
      `);
      ventanaImpresion.document.close();
    }
  }

  private generarFacturaHTML(pedido: Pedido): string {
    const fechaActual = new Date().toLocaleDateString('es-CO');
    const estadoConfig = this.getEstadoConfig(pedido.estado);
    
    return `
      <div class="factura-header">
        <div class="logo">🌱 AgroTienda</div>
        <div class="info-empresa">
          <p>Empresa de Productos Agrícolas</p>
          <p>Bogotá, Colombia</p>
          <p>NIT: 900.123.456-7</p>
          <p>Tel: +57 (1) 234-5678</p>
        </div>
      </div>

      <div class="factura-info">
        <div class="info-block">
          <div class="info-title">FACTURA DE VENTA</div>
          <p><strong>Número:</strong> ${pedido.id}</p>
          <p><strong>Fecha de Pedido:</strong> ${this.formatearFecha(pedido.fecha)}</p>
          <p><strong>Fecha de Factura:</strong> ${fechaActual}</p>
          <p><strong>Estado:</strong> <span class="estado-badge estado-${pedido.estado}">${estadoConfig.icono} ${estadoConfig.texto}</span></p>
        </div>
        
        <div class="info-block">
          <div class="info-title">INFORMACIÓN DE ENTREGA</div>
          <p><strong>Dirección:</strong><br>${pedido.direccionEntrega}</p>
          <p><strong>Método de Pago:</strong> ${pedido.metodoPago}</p>
        </div>
      </div>

      <table class="productos-table">
        <thead>
          <tr>
            <th>Producto</th>
            <th>Descripción</th>
            <th>Proveedor</th>
            <th>Cantidad</th>
            <th>Precio Unit.</th>
            <th>Subtotal</th>
          </tr>
        </thead>
        <tbody>
          ${pedido.productos.map(producto => `
            <tr>
              <td>
                <strong>${producto.nombre}</strong><br>
                <small>${producto.categoria}</small>
              </td>
              <td>${producto.descripcion}</td>
              <td>${producto.proveedor}</td>
              <td>${producto.cantidad} ${producto.unidad}</td>
              <td>${producto.precio.toFixed(2)}</td>
              <td>${this.calcularSubtotal(producto).toFixed(2)}</td>
            </tr>
          `).join('')}
        </tbody>
      </table>

      <div class="total-section">
        <p><strong>Subtotal:</strong> ${pedido.total.toFixed(2)}</p>
        <p><strong>IVA (0%):</strong> $0.00</p>
        <p><strong>Envío:</strong> Gratis</p>
        <div class="total-final">
          <strong>TOTAL A PAGAR: ${pedido.total.toFixed(2)}</strong>
        </div>
      </div>

      <div class="footer">
        <p>Gracias por su compra. Esta factura fue generada automáticamente.</p>
        <p>Para consultas: info@agrotienda.com | WhatsApp: +57 300 123 4567</p>
        <p>© 2025 AgroTienda - Todos los derechos reservados</p>
      </div>
    `;
  }

}