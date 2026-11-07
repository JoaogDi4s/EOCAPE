import { ComponentFixture, TestBed } from '@angular/core/testing';
import { vi } from 'vitest';
import { Eoquipe } from './eoquipe';

describe('Eoquipe', () => {
  let component: Eoquipe;
  let fixture: ComponentFixture<Eoquipe>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Eoquipe],
    }).compileComponents();

    fixture = TestBed.createComponent(Eoquipe);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render six member selectors and select the chosen member', () => {
    const buttons: NodeListOf<HTMLButtonElement> =
      fixture.nativeElement.querySelectorAll('.carousel-dot');

    expect(buttons.length).toBe(6);

    buttons[1].click();
    fixture.detectChanges();

    expect(component.activeIndex()).toBe(1);
    expect(buttons[1].getAttribute('aria-current')).toBe('true');
    expect(fixture.nativeElement.textContent).toContain('Front-end');
  });

  it('should render the matching portrait for every team member', () => {
    const portraits: NodeListOf<HTMLImageElement> =
      fixture.nativeElement.querySelectorAll('.member-portrait');

    expect(Array.from(portraits, (portrait) => portrait.getAttribute('src'))).toEqual([
      '/images/eoquipe/scrum_master.png',
      '/images/eoquipe/QA_analyst.png',
      '/images/eoquipe/analista_BI.png',
      '/images/eoquipe/UXUI_designer.png',
      '/images/eoquipe/gestora_viabilidade.png',
      '/images/eoquipe/especialista_seguranca.png',
    ]);
  });

  it('should advance every five seconds and reverse at the end of the carousel', () => {
    vi.useFakeTimers();
    try {
      component.ngOnInit();
      vi.advanceTimersByTime(5000);
      expect(component.activeIndex()).toBe(1);

      vi.advanceTimersByTime(25000);
      expect(component.activeIndex()).toBe(4);
    } finally {
      component.ngOnDestroy();
      vi.useRealTimers();
    }
  });

  it('should alternate slide directions and keep the last card stationary', () => {
    expect(component.slideDirection(0)).toBe('right');
    expect(component.slideDirection(1)).toBe('left');
    expect(component.slideDirection(2)).toBe('right');
    expect(component.slideDirection(5)).toBe('none');
  });

  it('should ignore indexes outside the member list', () => {
    component.selectMember(6);
    component.selectMember(-1);

    expect(component.activeIndex()).toBe(0);
  });
});
