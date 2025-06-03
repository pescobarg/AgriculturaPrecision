import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-dashboard',
  imports: [],
  standalone: true,
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent {

  constructor(private router: Router) {}

  navigateToPanel() {
    this.router.navigate(['/panel']);
  }

  navigateToBlog() {
    this.router.navigate(['/blog']);
  }

  navigateToAdvisory() {
    this.router.navigate(['/asesorias']);
  }

  navigateToSearch() {
    this.router.navigate(['/buscar']);
  }
}