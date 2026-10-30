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
  ],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  constructor(private router: Router) {}

  get loginRouteActive(): boolean {
    return this.router.url.split('?')[0] === '/login';
  }

  irParaLogin() {
    this.router.navigate(['/login']);
  }
}
