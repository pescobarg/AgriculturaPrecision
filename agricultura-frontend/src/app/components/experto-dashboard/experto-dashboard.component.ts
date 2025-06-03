import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-experto-dashboard',
  standalone: true,
  imports: [],
  templateUrl: './experto-dashboard.component.html',
  styleUrls: ['./experto-dashboard.component.css']
})
export class ExpertoDashboardComponent {

  constructor(private router: Router) {}

  navigateToAppointments() {
    this.router.navigate(['/citas']);
  }

  navigateToBlog() {
    this.router.navigate(['/blog']);
  }
}