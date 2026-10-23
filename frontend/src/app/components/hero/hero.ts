import { Component, OnDestroy, OnInit, computed, signal } from '@angular/core';
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
      src: '/images/hero/analyze-data.png',
      alt: 'Mulher analisando dados em um tablet, representando a funcionalidade de análise de dados do aplicativo.',
    },
    {
      src: '/images/hero/listening-to-feedback.png',
      alt: 'Mulher ouvindo feedback de clientes, representando a funcionalidade de coleta de feedback.',
    },
    {
      src: '/images/hero/home-control.png',
      alt: 'Homem controlando seu apartamento a distância, representando a funcionalidade de controle remoto.',
    },
  ];

  private mqMd = window.matchMedia('(min-width: 768px)');
  private mqLg = window.matchMedia('(min-width: 1024px)');

  private calcularDestaques() {
    if (this.mqLg.matches) return 1;
    if (this.mqMd.matches) return 1;
    return 1;
  }

  destaques = signal(this.calcularDestaques());
  private ultimaPosicao = computed(() => this.slides.length - this.destaques());

  indiceAtual = signal(0);
  private direcao = 1;
  private intervalo?: ReturnType<typeof setInterval>;

  private aoMudarTela = () => {
    this.destaques.set(this.calcularDestaques());
    if (this.indiceAtual() > this.ultimaPosicao()) {
      this.indiceAtual.set(this.ultimaPosicao());
    }
  };

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.mqMd.addEventListener('change', this.aoMudarTela);
    this.mqLg.addEventListener('change', this.aoMudarTela);
    this.iniciarAutoplay();
  }

  ngOnDestroy(): void {
    this.mqMd.removeEventListener('change', this.aoMudarTela);
    this.mqLg.removeEventListener('change', this.aoMudarTela);
    this.pararAutoplay();
  }

  destacado(i: number): boolean {
    return i >= this.indiceAtual() && i < this.indiceAtual() + this.destaques();
  }

  irParaSlide(i: number): void {
    this.irParaPosicao(Math.min(i, this.ultimaPosicao()));
  }

  private irParaPosicao(i: number): void {
    this.indiceAtual.set(i);
    if (i === this.ultimaPosicao()) this.direcao = -1;
    if (i === 0) this.direcao = 1;
    this.iniciarAutoplay();
  }

  private proximo(): void {
    let prox = this.indiceAtual() + this.direcao;
    if (prox > this.ultimaPosicao() || prox < 0) {
      this.direcao *= -1;
      prox = this.indiceAtual() + this.direcao;
    }
    this.indiceAtual.set(prox);
  }

  private iniciarAutoplay(): void {
    this.pararAutoplay();
    this.intervalo = setInterval(() => this.proximo(), 5000);
  }

  private pararAutoplay(): void {
    if (this.intervalo) {
      clearInterval(this.intervalo);
    }
  }

  irParaLogin(): void {
    this.router.navigate(['/login']);
  }
}
