// ===== product.service.ts =====
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

export interface Product {
  id?: number;
  name: string;
  description?: string;
  price: number;
  stock: number;
  ownerId?: number;
  ownerUsername?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface ProductCreateDTO {
  name: string;
  description?: string;
  price: number;
  stock: number;
}

export interface ProductUpdateDTO {
  name?: string;
  description?: string;
  price?: number;
  stock?: number;
}

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private readonly API_URL = 'http://localhost:8084/api/products';

  constructor(
    private http: HttpClient, 
    private authService: AuthService
  ) {}

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getStoredToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // Crear producto
  createProduct(product: ProductCreateDTO): Observable<Product> {
    const headers = this.getAuthHeaders();
    return this.http.post<Product>(this.API_URL, product, { headers });
  }

  // Obtener todos los productos
  getAllProducts(): Observable<Product[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<Product[]>(this.API_URL, { headers });
  }

  // Obtener mis productos
  getMyProducts(): Observable<Product[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<Product[]>(`${this.API_URL}/my-products`, { headers });
  }

  // Obtener producto por ID
  getProductById(id: number): Observable<Product> {
    const headers = this.getAuthHeaders();
    return this.http.get<Product>(`${this.API_URL}/${id}`, { headers });
  }

  // Actualizar producto
  updateProduct(id: number, product: ProductUpdateDTO): Observable<Product> {
    const headers = this.getAuthHeaders();
    return this.http.put<Product>(`${this.API_URL}/${id}`, product, { headers });
  }

  // Eliminar producto
  deleteProduct(id: number): Observable<void> {
    const headers = this.getAuthHeaders();
    return this.http.delete<void>(`${this.API_URL}/${id}`, { headers });
  }
}