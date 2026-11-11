export interface LoginRequest {
    username: string;
    password: string;
}

export interface RegisterRequest {
    username: string;
    password: string;
}

/** ตรงกับ AuthResponse.java */
export interface AuthResponse {
    token: string;
    tokenType: string;
    expiresIn: number;
    username: string;
}

export interface ApiError {
    status: number;
    error: string;
    message?: string;
    errors?: Record<string, string>; // มีเฉพาะตอน validation ผิด (400) → ต้องใส่ ? ให้ตรงความจริง
}