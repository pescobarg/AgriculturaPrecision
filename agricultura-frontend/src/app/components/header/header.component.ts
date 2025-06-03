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
        { label: 'Asesoría', icon: '💡', action: () => this.irAsesoria() },
        { label: 'Dashboard', icon: '📊', action: () => this.irDashboardAgricultor() }
      );
    }

    // PROVEEDOR: Productos, Blog, Asesoria
    if (roles.includes('PROVEEDOR')) {
      this.navItems.push(
        { label: 'Productos', icon: '📦', action: () => this.irProductos() },
        { label: 'Blog', icon: '📝', action: () => this.irBlog() },
        { label: 'Asesoría', icon: '💡', action: () => this.irAsesoria() },
        { label: 'Dashboard', icon: '📊', action: () => this.irDashboardProveedor() }
      );
    }

    // EXPERTO: Citas, Blog
    if (roles.includes('EXPERTO')) {
      this.navItems.push(
        { label: 'Citas', icon: '📅', action: () => this.irCitas() },
        { label: 'Blog', icon: '📝', action: () => this.irBlog() },
        { label: 'Dashboard', icon: '📊', action: () => this.irDashboardExperto() }
      );
    }

    // ADMINISTRADOR: Todas las anteriores + Panel de Control
    if (roles.includes('ADMINISTRADOR')) {
      this.navItems = [
        { label: 'Panel', icon: '⚙️', action: () => this.irPanelControl() },
        { label: 'Productos', icon: '📦', action: () => this.irBuscar() },
        { label: 'Blog', icon: '📝', action: () => this.irBlog() },
        { label: 'Asesoría', icon: '💡', action: () => this.irAsesoria() },
        { label: 'Dashboard', icon: '📊', action: () => this.irDashboardAdmin() }

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
    this.router.navigate(['/sensores']);
  }

  irBuscar() {
    this.router.navigate(['/buscar']);
  }

  irProductos() {
    this.router.navigate(['/productos']);
  }

  irCitas() {
    this.router.navigate(['/citas']);
  }

  irBlog() {
    this.router.navigate(['/blog']);
  }

  irAsesoria() {
    this.router.navigate(['/asesorias']);
  }


  irPanelControl() {
    this.router.navigate(['/panel']);
  }


  
  irDashboardAgricultor() { 
    this.router.navigate(['/agricultor-dashboard']); 
  }
  
  irDashboardAdmin() { 
    this.router.navigate(['/admin-dashboard']); 
  }

  irDashboardProveedor() { 
    this.router.navigate(['/proveedor-dashboard']); 
  }

  irDashboardExperto() { 
    this.router.navigate(['/experto-dashboard']); 
  }

  logout() {
    this.authService.logout();
    this.isLoggedIn = false;
    this.currentUser = null;
    this.navItems = [];
  }
}