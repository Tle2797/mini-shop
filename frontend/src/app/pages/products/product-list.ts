import { DecimalPipe } from '@angular/common';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';

import { environment } from '../../../environments/environment';
import { AuthService } from '../../core/services/auth.service';
import { getErrorMessage } from '../../core/utils/error.util';

// ⚠️ ชั่วคราว: ขั้นที่ 7 จะย้าย HTTP ไปไว้ใน ProductService และทำ CRUD เต็มรูปแบบ
@Component({
  selector: 'app-product-list',
  imports: [DecimalPipe], // ใช้ pipe "number" ใน template ต้อง import ก่อน
  templateUrl: './product-list.html',
})
export class ProductList implements OnInit {
  protected readonly auth = inject(AuthService);
  private readonly http = inject(HttpClient);

  protected readonly products = signal<{ id: number; name: string; price: number }[]>([]);
  protected readonly errorMessage = signal<string | null>(null);

  // ngOnInit = ทำงาน 1 ครั้งตอน component ถูกสร้างเสร็จ
  ngOnInit(): void {
    // สังเกต: ไม่ได้แนบ token เองเลย → interceptor ทำให้
    this.http.get<{ id: number; name: string; price: number }[]>(`${environment.apiUrl}/products`)
      .subscribe({
        next: (data) => this.products.set(data),
        error: (err: HttpErrorResponse) => {
          console.error('[ProductList] load failed:', err);
          this.errorMessage.set(getErrorMessage(err));
        },
      });
  }
}