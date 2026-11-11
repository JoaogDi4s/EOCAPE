import { Component } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { Navbar } from './components/navbar/navbar';
import { Footer } from './components/footer/footer';
import { Hero } from './components/hero/hero';
import { Benefits } from './components/benefits/benefits';
import { HowItWorks } from './components/how-it-works/how-it-works';
import { CallToAction } from './components/call-to-action/call-to-action';
import { whiteButton } from './components/WhiteButton/whiteButton';
import { NewMemberForm } from './components/Lead/lead-forms/lead-forms';
import { Eoquipe } from './components/eoquipe/eoquipe';
import { WhatsappButton } from './components/whatsapp-button/whatsapp-button';

@Component({
  imports: [
    RouterOutlet,
    RouterLink,
    Navbar,
    Footer,
    whiteButton,
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
  constructor(private router: Router) {}

  get standaloneRouteActive(): boolean {
    const currentPath = this.router.url.split('?')[0];
    return (
      currentPath === '/login' || currentPath === '/boas-vindas' || currentPath === '/user-page'
    );
  }

  irParaLogin() {
    this.router.navigate(['/login']);
  }
}
