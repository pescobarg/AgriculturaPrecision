import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-proveedor-dashboard',
  standalone: true,
  imports: [],
  templateUrl: './proveedor-dashboard.component.html',
  styleUrls: ['./proveedor-dashboard.component.css']
})
export class ProveedorDashboardComponent {

  constructor(private router: Router) {}

  navigateToProducts() {
    this.router.navigate(['/productos']);
  }

  navigateToBlog() {
    this.router.navigate(['/blog']);
  }

  navigateToAdvisory() {
    this.router.navigate(['/asesorias']);
  }
}