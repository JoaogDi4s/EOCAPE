import { TestBed } from '@angular/core/testing';
import { ThemeService } from './theme-service';
describe('ThemeService', () => {
  beforeEach(() => {
    localStorage.removeItem('eocape-theme');
    document.documentElement.removeAttribute('data-theme');
  });
  afterEach(() => {
    TestBed.resetTestingModule();
    localStorage.removeItem('eocape-theme');
    document.documentElement.dataset['theme'] = 'light';
    document.documentElement.style.colorScheme = 'light';
  });
  it('uses the saved theme and applies it to native controls', () => {
    localStorage.setItem('eocape-theme', 'dark');
    const theme = TestBed.inject(ThemeService);
    expect(theme.isDark()).toBe(true);
    expect(document.documentElement.dataset['theme']).toBe('dark');
    expect(document.documentElement.style.colorScheme).toBe('dark');
  });
  it('persists explicit changes and restores light mode', () => {
    const theme = TestBed.inject(ThemeService);
    theme.setTheme('dark');
    expect(localStorage.getItem('eocape-theme')).toBe('dark');
    theme.toggle();
    expect(localStorage.getItem('eocape-theme')).toBe('light');
    expect(theme.isDark()).toBe(false);
  });
  it('follows system changes until the user selects a theme', () => {
    let listener: ((event: { matches: boolean }) => void) | undefined;
    const media = {
      matches: true,
      addEventListener: vi.fn((_: string, fn: typeof listener) => (listener = fn)),
      removeEventListener: vi.fn(),
    };
    vi.mocked(window.matchMedia).mockReturnValue(media as unknown as MediaQueryList);
    const theme = TestBed.inject(ThemeService);
    expect(theme.isDark()).toBe(true);
    listener?.({ matches: false });
    expect(theme.isDark()).toBe(false);
    theme.setTheme('dark');
    listener?.({ matches: false });
    expect(theme.isDark()).toBe(true);
  });
  it('continues to switch when browser storage is blocked', () => {
    vi.spyOn(Storage.prototype, 'getItem').mockImplementation(() => {
      throw new Error('blocked');
    });
    vi.spyOn(Storage.prototype, 'setItem').mockImplementation(() => {
      throw new Error('blocked');
    });
    const theme = TestBed.inject(ThemeService);
    expect(() => theme.setTheme('dark')).not.toThrow();
    expect(theme.isDark()).toBe(true);
    vi.restoreAllMocks();
  });
  it('synchronizes changes from another tab', () => {
    const theme = TestBed.inject(ThemeService);
    localStorage.setItem('eocape-theme', 'dark');
    window.dispatchEvent(new StorageEvent('storage', { key: 'eocape-theme', newValue: 'dark' }));
    expect(theme.isDark()).toBe(true);
  });
});
