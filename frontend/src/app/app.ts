import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { environment } from '../environments/environment.development';

@Component({
  imports: [RouterOutlet],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
    // signal = ตัวแปรที่ Angular ติดตามการเปลี่ยนแปลง แล้วอัปเดตหน้าจอให้อัตโนมัติ
  protected readonly title = signal('Mini Shop');
  protected readonly apiUrl = environment.apiUrl;
  protected readonly clicks = signal(0);

  increase() {
    this.clicks.update((n) => n + 1);
  }
}

