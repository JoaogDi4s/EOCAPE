import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { UserSession } from '../../auth/user-session';

@Component({
  imports: [],
  selector: 'app-user-page',
  styleUrl: './user-page.css',
  templateUrl: './user-page.html',
})
export class UserPage {
  private readonly router = inject(Router);
  private readonly userSession = inject(UserSession);

  activeSection: 'home' | 'configuracoes' | 'sobre' = 'home';
  selectedService: string | null = null;
  private previousSection: 'home' | 'configuracoes' | 'sobre' = 'home';

  selectSection(section: 'home' | 'configuracoes' | 'sobre'): void {
    this.selectedService = null;
    this.activeSection = section;
  }

  openService(service: string): void {
    this.previousSection = this.activeSection;
    this.selectedService = service;
  }

  returnToPreviousContent(): void {
    this.selectedService = null;
    this.activeSection = this.previousSection;
  }

  sair(): void {
    this.userSession.end();
    this.router.navigate(['/login'], { replaceUrl: true });
  }
}
