import { ThemeService } from './services/theme-service';
import { Component, inject } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { Navbar } from './components/navbar/navbar';
import { Footer } from './components/footer/footer';
import { Hero } from './components/hero/hero';
import { Benefits } from './components/benefits/benefits';
import { HowItWorks } from './components/how-it-works/how-it-works';
import { CallToAction } from './components/call-to-action/call-to-action';
import { NewMemberForm } from './components/Lead/lead-forms/lead-forms';
import { Eoquipe } from './components/eoquipe/eoquipe';
import { WhatsappButton } from './components/whatsapp-button/whatsapp-button';

@Component({
  imports: [
    RouterOutlet,
    Navbar,
    Footer,
    Hero,
    Benefits,
    HowItWorks,
    CallToAction,
    NewMemberForm,
    WhatsappButton,
    Eoquipe,
  ],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  readonly theme = inject(ThemeService);
  constructor(private router: Router) {}

  get standaloneRouteActive(): boolean {
    const currentPath = this.router.url.split('?')[0];
    return (
      currentPath === '/login' ||
      currentPath === '/boas-vindas' ||
      currentPath === '/user-page' ||
      currentPath.startsWith('/user-page/')
    );
  }

  irParaLogin() {
    this.router.navigate(['/login']);
  }
}
