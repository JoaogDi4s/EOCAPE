import { Component } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { Navbar } from './components/navbar/navbar';
import { Footer } from './components/footer/footer';
import { Hero } from './components/hero/hero';
import { whiteButton } from './components/WhiteButton/whiteButton';

@Component({
  imports: [RouterOutlet, RouterLink, Navbar, Footer, whiteButton, Hero],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  constructor(private router: Router) {}

  irParaLogin() {
    this.router.navigate(['/login']);
  }
}
