import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter, Routes } from '@angular/router';
import { App } from './app/app';
import { Register } from './app/components/register/register';
import { Login } from './app/components/login/login';
import { Dashboard } from './app/components/dashboard/dashboard';

const routes: Routes = [
  { path: 'register', component: Register },
  { path: 'login', component: Login },
  { path: 'dashboard', component: Dashboard },
  { path: '', redirectTo: 'register', pathMatch: 'full' }
];

bootstrapApplication(App, {
  providers: [
    provideHttpClient(),
    provideRouter(routes)
  ]
}).catch(err => console.error(err));