import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth.models';

const TOKEN_KEY = 'minishop_token';
const USERNAME_KEY = 'minishop_username';
const EXPIRES_AT_KEY = 'minishop_expires_at'; // เวลาที่ token หมดอายุ (ms นับจาก epoch)

// providedIn: 'root' = มี instance เดียวทั้งแอป (singleton) ทุก component ใช้ตัวเดียวกัน
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly baseUrl = `${environment.apiUrl}/auth`;

  // อ่านค่าจาก localStorage ตอนเปิดแอป → refresh หน้าแล้วยัง login อยู่
  private readonly tokenSignal = signal<string | null>(localStorage.getItem(TOKEN_KEY));
  readonly username = signal<string | null>(localStorage.getItem(USERNAME_KEY));
  private readonly expiresAt = signal<number>(Number(localStorage.getItem(EXPIRES_AT_KEY) ?? 0));

  // ต้องมี token และยังไม่หมดอายุ จึงจะถือว่า login อยู่
  // ⚠️ ใช้ method ธรรมดาแทน computed เพราะ computed จะ cache ค่าไว้
  //    และไม่รู้ว่า "เวลาผ่านไป" (Date.now() ไม่ใช่ signal) → token หมดอายุแล้วก็ยังได้ true
  isLoggedIn(): boolean {
    return this.tokenSignal() !== null && Date.now() < this.expiresAt();
  }

  get token(): string | null {
    return this.tokenSignal();
  }

  login(body: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.baseUrl}/login`, body)
      .pipe(tap((res) => this.saveSession(res))); // tap = แอบทำอะไรบางอย่างกับผลลัพธ์ โดยไม่เปลี่ยนมัน
  }

  register(body: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.baseUrl}/register`, body)
      .pipe(tap((res) => this.saveSession(res)));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USERNAME_KEY);
    localStorage.removeItem(EXPIRES_AT_KEY);
    this.tokenSignal.set(null);
    this.username.set(null);
    this.expiresAt.set(0);
    this.router.navigate(['/login']);
  }

  private saveSession(res: AuthResponse): void {
    const expiresAt = Date.now() + res.expiresIn; // expiresIn จาก backend เป็นมิลลิวินาที
    localStorage.setItem(TOKEN_KEY, res.token);
    localStorage.setItem(USERNAME_KEY, res.username);
    localStorage.setItem(EXPIRES_AT_KEY, String(expiresAt));
    this.tokenSignal.set(res.token);
    this.username.set(res.username);
    this.expiresAt.set(expiresAt);
  }
}