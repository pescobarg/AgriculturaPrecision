import { CommonModule } from '@angular/common';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService, UserInfo } from '../../services/auth.service';
import { Subscription } from 'rxjs';

interface NavItem {
  label: string;
  icon: string;
  action: () => void;
}

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit, OnDestroy {
  isLoggedIn = false;
  currentUser: UserInfo | null = null;
  navItems: NavItem[] = [];
  private userSubscription?: Subscription;

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit() {
    // Suscribirse a los cambios del usuario actual
    this.userSubscription = this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.isLoggedIn = !!user;
      this.updateNavItems();
    });

    // Verificar si hay token almacenado
    if (typeof window !== 'undefined' && window.localStorage) {
      this.isLoggedIn = !!localStorage.getItem('access_token');
      if (this.isLoggedIn && !this.currentUser) {
        // Si hay token pero no hay usuario actual, obtener la info del usuario
        this.authService.getUserInfo().subscribe({
          next: (user) => {
            this.currentUser = user;
            this.updateNavItems();
          },
          error: () => {
            this.logout();
          }
        });
      }
    }
  }

  ngOnDestroy() {
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
  }

  private updateNavItems() {
    this.navItems = [];
    
    if (!this.currentUser || !this.currentUser.roles) {
      return;
    }

    const roles = this.currentUser.roles;

    // AGRICULTOR: Sensores, Buscar, Blog, Asesoria
    if (roles.includes('AGRICULTOR')) {
      this.navItems.push(
        { label: 'Sensores', icon: '📡', action: () => this.irSensores() },
        { label: 'Buscar', icon: '🔍', action: () => this.irBuscar() },
        { label: 'Blog', icon: '📝', action: () => this.irBlog() },
        { label: 'Asesoría', icon: '💡', action: () => this.irAsesoria() }
      );
    }

    // PROVEEDOR: Productos, Blog, Asesoria
    if (roles.includes('PROVEEDOR')) {
      this.navItems.push(
        { label: 'Productos', icon: '📦', action: () => this.irProductos() },
        { label: 'Blog', icon: '📝', action: () => this.irBlog() },
        { label: 'Asesoría', icon: '💡', action: () => this.irAsesoria() }
      );
    }

    // EXPERTO: Citas, Blog
    if (roles.includes('EXPERTO')) {
      this.navItems.push(
        { label: 'Citas', icon: '📅', action: () => this.irCitas() },
        { label: 'Blog', icon: '📝', action: () => this.irBlog() }
      );
    }

    // ADMINISTRADOR: Todas las anteriores + Panel de Control
    if (roles.includes('ADMINISTRADOR')) {
      this.navItems = [
        { label: 'Panel', icon: '⚙️', action: () => this.irPanelControl() },
        { label: 'Sensores', icon: '📡', action: () => this.irSensores() },
        { label: 'Productos', icon: '📦', action: () => this.irProductos() },
        { label: 'Blog', icon: '📝', action: () => this.irBlog() },
        { label: 'Asesoría', icon: '💡', action: () => this.irAsesoria() }
      ];
    }
  }

  // Métodos de navegación existentes
  irHome() { 
    this.router.navigate(['/home']); 
  }

  irLogin() { 
    this.router.navigate(['/login']); 
  }

  irRegister() { 
    this.router.navigate(['/register']); 
  }

  // Nuevos métodos de navegación según roles
  irSensores() {
    this.router.navigate(['/home']);
  }

  irBuscar() {
    this.router.navigate(['/home']);
  }

  irProductos() {
    this.router.navigate(['/home']);
  }

  irCitas() {
    this.router.navigate(['/home']);
  }

  irBlog() {
    this.router.navigate(['/home']);
  }

  irAsesoria() {
    this.router.navigate(['/home']);
  }

  irPanelControl() {
    this.router.navigate(['/home']);
  }

  irNotificaciones() {
    this.router.navigate(['/home']);
  }

  logout() {
    this.authService.logout();
    this.isLoggedIn = false;
    this.currentUser = null;
    this.navItems = [];
  }
}