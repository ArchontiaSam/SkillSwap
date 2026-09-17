import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { ApiService } from '../../services/api';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  styleUrl: './login.css',
  templateUrl: './login.html',
})
export class Login {
  user = {
    email: '',
    password: ''
  };

  errorMessage: string = '';

  constructor(private apiService: ApiService, private router: Router) {}

 onLogin(): void {
  this.apiService.login(this.user).subscribe({
    next: (response: any) => {
      console.log('Login successful', response);

      if (response?.id) {
        localStorage.setItem('userId', response.id.toString());
      }
      if (response?.name) {
        localStorage.setItem('userName', response.name);
      }
      if (response?.email) {
        localStorage.setItem('userEmail', response.email);
      }

      this.router.navigate(['/dashboard']);
    },
    error: (error: any) => {
      console.error('Login error', error);
      this.errorMessage = 'Login failed. Wrong email or password.';
    }
  });
}
}