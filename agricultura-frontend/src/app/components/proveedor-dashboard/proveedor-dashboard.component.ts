import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService, Product, ProductCreateDTO, ProductUpdateDTO } from './../../services/product.service';
import { AuthService } from '../../services/auth.service'; 

@Component({
  selector: 'app-proveedor-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './proveedor-dashboard.component.html',
  styleUrls: ['./proveedor-dashboard.component.css']
})
export class ProveedorDashboardComponent implements OnInit {
  products: Product[] = [];
  isLoading = false;
  showCreateForm = false;
  showEditForm = false;
  currentProduct: Product | null = null;
  
  // Formulario para crear/editar
  productForm: ProductCreateDTO = {
    name: '',
    description: '',
    price: 0,
    stock: 0
  };

  // Mensajes
  successMessage = '';
  errorMessage = '';

  constructor(
    private productService: ProductService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadMyProducts();
  }

  // Cargar mis productos
  loadMyProducts(): void {
    this.isLoading = true;
    this.productService.getMyProducts().subscribe({
      next: (products) => {
        this.products = products;
        this.isLoading = false;
      },
      error: (error) => {
        this.showError('Error al cargar los productos');
        this.isLoading = false;
      }
    });
  }

  // Mostrar formulario de creación
  showCreateProductForm(): void {
    this.resetForm();
    this.showCreateForm = true;
    this.showEditForm = false;
  }

  // Mostrar formulario de edición
  showEditProductForm(product: Product): void {
    this.currentProduct = product;
    this.productForm = {
      name: product.name,
      description: product.description || '',
      price: product.price,
      stock: product.stock
    };
    this.showEditForm = true;
    this.showCreateForm = false;
  }

  // Crear producto
  createProduct(): void {
    if (!this.validateForm()) return;

    this.isLoading = true;
    this.productService.createProduct(this.productForm).subscribe({
      next: (product) => {
        this.products.unshift(product);
        this.showSuccess('Producto creado exitosamente');
        this.cancelForm();
        this.isLoading = false;
      },
      error: (error) => {
        this.showError('Error al crear el producto');
        this.isLoading = false;
      }
    });
  }

  // Actualizar producto
  updateProduct(): void {
    if (!this.currentProduct || !this.validateForm()) return;

    this.isLoading = true;
    const updateData: ProductUpdateDTO = {
      name: this.productForm.name,
      description: this.productForm.description,
      price: this.productForm.price,
      stock: this.productForm.stock
    };

    this.productService.updateProduct(this.currentProduct.id!, updateData).subscribe({
      next: (updatedProduct) => {
        const index = this.products.findIndex(p => p.id === updatedProduct.id);
        if (index !== -1) {
          this.products[index] = updatedProduct;
        }
        this.showSuccess('Producto actualizado exitosamente');
        this.cancelForm();
        this.isLoading = false;
      },
      error: (error) => {
        this.showError('Error al actualizar el producto');
        this.isLoading = false;
      }
    });
  }

  // Eliminar producto
  deleteProduct(product: Product): void {
    if (confirm(`¿Estás seguro de que quieres eliminar "${product.name}"?`)) {
      this.isLoading = true;
      this.productService.deleteProduct(product.id!).subscribe({
        next: () => {
          this.products = this.products.filter(p => p.id !== product.id);
          this.showSuccess('Producto eliminado exitosamente');
          this.isLoading = false;
        },
        error: (error) => {
          this.showError('Error al eliminar el producto');
          this.isLoading = false;
        }
      });
    }
  }

  // Validar formulario
  validateForm(): boolean {
    if (!this.productForm.name.trim()) {
      this.showError('El nombre del producto es requerido');
      return false;
    }
    if (this.productForm.price <= 0) {
      this.showError('El precio debe ser mayor a 0');
      return false;
    }
    if (this.productForm.stock < 0) {
      this.showError('El stock no puede ser negativo');
      return false;
    }
    return true;
  }

  // Cancelar formulario
  cancelForm(): void {
    this.resetForm();
    this.showCreateForm = false;
    this.showEditForm = false;
    this.currentProduct = null;
  }

  // Resetear formulario
  resetForm(): void {
    this.productForm = {
      name: '',
      description: '',
      price: 0,
      stock: 0
    };
  }

  // Mostrar mensaje de éxito
  showSuccess(message: string): void {
    this.successMessage = message;
    this.errorMessage = '';
    setTimeout(() => this.successMessage = '', 3000);
  }

  // Mostrar mensaje de error
  showError(message: string): void {
    this.errorMessage = message;
    this.successMessage = '';
    setTimeout(() => this.errorMessage = '', 3000);
  }

  // Obtener usuario actual
  getCurrentUser() {
    return this.authService.getCurrentUser();
  }

  trackByProductId(index: number, product: Product): number {
  return product.id!;
}

}