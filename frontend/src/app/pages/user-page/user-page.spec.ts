import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { UserPage } from './user-page';
import { UserSession } from '../../auth/user-session';

describe('UserPage', () => {
  let component: UserPage;
  let fixture: ComponentFixture<UserPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserPage],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(UserPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should end the session and replace the current route on logout', () => {
    const session = TestBed.inject(UserSession);
    const router = TestBed.inject(Router);
    session.start();
    const navigateSpy = vi.spyOn(router, 'navigate').mockResolvedValue(true);

    component.sair();

    expect(session.isAuthenticated()).toBe(false);
    expect(navigateSpy).toHaveBeenCalledWith(['/login'], { replaceUrl: true });
  });
});
