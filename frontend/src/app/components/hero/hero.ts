import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { whiteButton } from '../WhiteButton/whiteButton';

@Component({
  imports: [whiteButton],
  selector: 'app-hero',
  templateUrl: './hero.html',
})
export class Hero implements OnInit, OnDestroy {
  slides = [
    {
      src: 'assets/images/hero-image.png',
      alt: 'Icone de um prédio com uma mão segurando um celular, representando a gestão de condomínios na palma da mão',
    },
    { src: 'assets/images/hero-2.png', alt: 'Descrição da segunda imagem' },
    { src: 'assets/images/hero-3.png', alt: 'Descrição da terceira imagem' },
  ];

  indiceAtual = 0;
  intervalo?: ReturnType<typeof setInterval>;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.iniciarAutoplay();
  }

  ngOnDestroy(): void {
    this.pararAutoplay();
  }

  irParaSlide(i: number): void {
    this.indiceAtual = i;
    this.iniciarAutoplay();
  }

  proximo(): void {
    this.indiceAtual = (this.indiceAtual + 1) % this.slides.length;
  }

  iniciarAutoplay(): void {
    this.pararAutoplay();
    this.intervalo = setInterval(() => this.proximo(), 5000);
  }

  pararAutoplay(): void {
    if (this.intervalo) {
      clearInterval(this.intervalo);
    }
  }

  irParaLogin(): void {
    this.router.navigate(['/login']);
  }
}
