import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-agricultor-dashboard',
  imports: [],
  standalone: true,
  templateUrl: './agricultor-dashboard.component.html',
  styleUrl: './agricultor-dashboard.component.css'
})
export class AgricultorDashboardComponent {

  constructor(private router: Router) {}

  navigateToSensors() {
    this.router.navigate(['/sensores']);
  }

  navigateToProducts() {
    this.router.navigate(['/buscar']);
  }

  navigateToBlog() {
    this.router.navigate(['/blog']);
  }

  navigateToAdvisory() {
    this.router.navigate(['/asesorias']);
  }
}