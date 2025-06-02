import { Component } from '@angular/core';
import { Router, RouterModule, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [RouterModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  constructor(private router: Router) {}

  irRegistro() {
    this.router.navigate(['/register']); 
  }

  irLogin() {
    this.router.navigate(['/login']);
  }
}
