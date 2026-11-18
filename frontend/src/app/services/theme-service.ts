import { DOCUMENT } from '@angular/common';
import { DestroyRef, Injectable, computed, inject, signal } from '@angular/core';
export type Theme = 'light' | 'dark';
/** Mantém o tema entre páginas e visitas, sem depender da autenticação. */
@Injectable({ providedIn: 'root' })
export class ThemeService {
  private readonly document = inject(DOCUMENT);
  private readonly destroyRef = inject(DestroyRef);
  private readonly window = this.document.defaultView;
  private readonly key = 'eocape-theme';
  private preference: Theme | null = null;
  private readonly current = signal<Theme>('light');
  readonly isDark = computed(() => this.current() === 'dark');
  constructor() {
    const media = this.window?.matchMedia?.('(prefers-color-scheme: dark)');
    this.preference = this.readPreference();
    this.apply(this.preference ?? (media?.matches ? 'dark' : 'light'));
    const onSystemChange = (event: MediaQueryListEvent) => {
      if (!this.preference) this.apply(event.matches ? 'dark' : 'light');
    };
    const onStorage = (event: StorageEvent) => {
      if (event.key !== this.key && event.key !== null) return;
      this.preference = this.readPreference();
      this.apply(this.preference ?? (media?.matches ? 'dark' : 'light'));
    };
    media?.addEventListener('change', onSystemChange);
    this.window?.addEventListener('storage', onStorage);
    this.destroyRef.onDestroy(() => {
      media?.removeEventListener('change', onSystemChange);
      this.window?.removeEventListener('storage', onStorage);
    });
  }
  toggle(): void {
    this.setTheme(this.isDark() ? 'light' : 'dark');
  }
  setTheme(theme: Theme): void {
    this.preference = theme;
    try {
      this.window?.localStorage.setItem(this.key, theme);
    } catch {
      /* O tema funciona mesmo com armazenamento bloqueado. */
    }
    this.apply(theme);
  }
  private readPreference(): Theme | null {
    try {
      const value = this.window?.localStorage.getItem(this.key);
      return value === 'dark' || value === 'light' ? value : null;
    } catch {
      return null;
    }
  }
  private apply(theme: Theme): void {
    this.current.set(theme);
    this.document.documentElement.dataset['theme'] = theme;
    this.document.documentElement.style.colorScheme = theme;
  }
}
