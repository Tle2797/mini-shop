import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';

import { environment } from '../../../environments/environment';
import { AuthService } from '../services/auth.service';

/** ทำงานกับ "ทุก" request ที่ส่งผ่าน HttpClient */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = auth.token;
  const isApiRequest = req.url.startsWith(environment.apiUrl);

  // request เป็น immutable → ต้อง clone แล้วเพิ่ม header
  const authReq =
    token && isApiRequest
      ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
      : req;

  return next(authReq).pipe(
    catchError((err: HttpErrorResponse) => {
      // token หมดอายุ/ไม่ถูกต้อง → ออกจากระบบอัตโนมัติ
      // (ยกเว้น /auth/... เพราะ login ผิดก็ได้ 401 แต่เราอยากโชว์ข้อความแทน)
      if (err.status === 401 && !req.url.includes('/auth/')) {
        auth.logout();
      }
      return throwError(() => err); // ส่ง error ต่อให้ component จัดการต่อ
    }),
  );
};