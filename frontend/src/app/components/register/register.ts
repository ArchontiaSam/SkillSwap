import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { ApiService } from '../../services/api';
import { FormsModule, NgForm } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  templateUrl: './register.html',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  styleUrls: ['./register.css']
})
export class Register {
  //object that saves data from the form
  user = {
    name:'',
    email: '',
    password: ''
  };

  successMessage: string = '';
  errorMessage: string = '';

  constructor(private apiService: ApiService, private router: Router) {}

  //Takes the form ad a parameter
  onRegister(form: NgForm) {
    this.errorMessage = '';
    this.successMessage = '';

    // Check for bugs
    if (form.invalid) {
      form.form.markAllAsTouched(); //  
      return;  
    }

    this.apiService.register(this.user).subscribe({
      next: (response) => {
        console.log('Registration successful!', response);
        this.successMessage = 'You have successfully registered! Redirecting to login...';
        
        //after 1 sec the page goes to login page
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 1000);
      },
      error: (error) => {
        console.error('Registration error', error);
        if (error.status === 400 || error.status === 409) {
          this.errorMessage = 'This email has already been used.';
        } else {
          this.errorMessage = 'Something went wrong, try again.';
        }
      }
    });
  }
}