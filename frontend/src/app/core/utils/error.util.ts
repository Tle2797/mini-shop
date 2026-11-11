import { HttpErrorResponse } from "@angular/common/http";
import { ApiError } from '../models/auth.models';

/** แปลง HttpErrorResponse → ข้อความสำหรับแสดงบนหน้าจอ */
export function getErrorMessage (err: HttpErrorResponse): string {
     // กรณีที่ 1: status = 0 แปลว่า request ไม่เคยได้รับคำตอบจาก server เลย
     // สาเหตุที่พบบ่อย:
     //   - backend ยังไม่ได้รัน / พอร์ตผิด
     //   - เบราว์เซอร์บล็อกเพราะ CORS (server ไม่อนุญาต origin ของ frontend)
     //   - เน็ตหลุด
     // ตอนนี้ไม่มี body ให้อ่าน จึงต้องเขียนข้อความเอง
     if (err.status === 0 ) {
        return 'เชื่อมต่อเซิร์ฟเวอร์ไม่ได้ (backend ไม่ได้รัน หรือโดน CORS บล็อก)';
     }

     // server ตอบกลับมาแล้ว → ลองอ่าน body ในรูปแบบ ApiError ที่ backend ส่งมา
     // (อาจเป็น null ได้ ถ้า server ไม่ได้ส่ง body กลับมา)
     const body = err.error as ApiError | null;

     // กรณีที่ 2: มี field "errors" → เป็น validation error (มักเป็น 400 Bad Request)
     // หน้าตาประมาณ { errors: { email: 'อีเมลไม่ถูกต้อง', password: 'สั้นเกินไป' } }
     // ดึงเฉพาะข้อความของแต่ละ field มาต่อกันด้วย ", " เพื่อแสดงทีเดียว
     if (body?.errors) {
        return Object.values(body.errors).join(', ');
     }

     // กรณีที่ 3: มี field "message" → error ทั่วไปที่ backend อธิบายมาให้แล้ว
     // เช่น 401 (รหัสผ่านผิด), 404 (ไม่พบข้อมูล), 409 (ข้อมูลซ้ำ)
     // ใช้ข้อความจาก backend ได้เลย
     if (body?.message) {
        return body.message;
     }

     // กรณีที่ 4 (fallback): server ตอบมาแต่ไม่มี body ในรูปแบบที่เรารู้จัก
     // เช่น 500 ที่ไม่มีข้อความ หรือ body เป็น HTML → แสดงแค่ status code แทน
     return `เกิดข้อผิดพลาด (Http ${err.status})`;
}
