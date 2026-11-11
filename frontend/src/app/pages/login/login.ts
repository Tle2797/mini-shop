import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../core/services/auth.service';
import { getErrorMessage } from '../../core/utils/error.util';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink], // ⚠️ ลืม ReactiveFormsModule = error (เราจะลองในส่วน C)
  templateUrl: './login.html',
})
export class Login {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly loading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  // nonNullable = ค่าไม่เป็น null (reset แล้วกลับเป็น '')
  protected readonly form = this.fb.nonNullable.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched(); // ให้ข้อความ error ขึ้นทุกช่อง
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    this.auth.login(this.form.getRawValue()).subscribe({
      next: () => this.router.navigate(['/products']),
      error: (err: HttpErrorResponse) => {
        console.error('[Login] failed:', err); // 👈 log ไว้ดูใน Console ตอนไล่ error
        this.errorMessage.set(getErrorMessage(err));
        this.loading.set(false);
      },
    });
  }
}