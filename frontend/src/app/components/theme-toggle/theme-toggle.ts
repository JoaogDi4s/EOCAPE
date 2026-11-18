import { Component, inject } from '@angular/core';
import { ThemeService } from '../../services/theme-service';
@Component({
  selector: 'app-theme-toggle',
  templateUrl: './theme-toggle.html',
  styleUrl: './theme-toggle.css',
})
export class ThemeToggle {
  readonly theme = inject(ThemeService);
}
