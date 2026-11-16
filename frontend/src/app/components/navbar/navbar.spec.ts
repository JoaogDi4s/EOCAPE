import { provideRouter } from '@angular/router';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Navbar } from './navbar';

describe('Navbar', () => {
  let component: Navbar;
  let fixture: ComponentFixture<Navbar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Navbar],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(Navbar);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('returns focus to the menu button on Escape and closes on outside click', async () => {
    const nav = fixture.nativeElement.querySelector('nav') as HTMLElement;
    const toggle = nav.querySelector('.menu-toggle') as HTMLButtonElement;
    const link = nav.querySelector('.menu-links a') as HTMLAnchorElement;
    expect(toggle.getAttribute('aria-expanded')).toBe('false');
    toggle.click();
    await fixture.whenStable();
    expect(toggle.getAttribute('aria-expanded')).toBe('true');
    link.focus();
    link.dispatchEvent(new KeyboardEvent('keydown', { key: 'Escape', bubbles: true }));
    await fixture.whenStable();
    expect(toggle.getAttribute('aria-expanded')).toBe('false');
    expect(document.activeElement).toBe(toggle);

    toggle.click();
    document.body.click();
    await fixture.whenStable();
    expect(nav.classList.contains('menu-open')).toBe(false);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
