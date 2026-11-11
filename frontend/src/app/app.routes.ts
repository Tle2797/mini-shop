import { Routes } from '@angular/router';

import { authGuard, guestGuard } from './core/guards/auth.guard';
import { Login } from './pages/login/login';
import { ProductList } from './pages/products/product-list';
import { Register } from './pages/register/register';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'products' },
  { path: 'login', component: Login, canActivate: [guestGuard] },
  { path: 'register', component: Register, canActivate: [guestGuard] },
  { path: 'products', component: ProductList, canActivate: [authGuard] },
  { path: '**', redirectTo: 'products' }, // URL ที่ไม่รู้จัก
];