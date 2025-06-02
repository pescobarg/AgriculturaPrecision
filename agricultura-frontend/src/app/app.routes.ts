import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ProveedorDashboardComponent } from './components/proveedor-dashboard/proveedor-dashboard.component';
import { ExpertoDashboardComponent } from './components/experto-dashboard/experto-dashboard.component';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';
import { AgricultorDashboardComponent } from './components/agricultor-dashboard/agricultor-dashboard.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'proveedor-dashboard', component: ProveedorDashboardComponent },
  { path: 'experto-dashboard', component: ExpertoDashboardComponent },
  { path: 'admin-dashboard', component: AdminDashboardComponent },
  { path: 'agricultor-dashboard', component: AgricultorDashboardComponent }
];