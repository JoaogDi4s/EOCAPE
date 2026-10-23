import { Component, OnDestroy, OnInit, computed, signal } from '@angular/core';

@Component({
  selector: 'app-benefits',
  templateUrl: './benefits.html',
})
export class Benefits implements OnInit, OnDestroy {
  cards = [
    {
      titulo: 'Avisos e Comunicados',
      texto:
        'Receba comunicados oficiais do condomínio direto no celular, com histórico organizado para consultar quando quiser.',
    },
    {
      titulo: 'Atas de Assembleia',
      texto:
        'Acesse o registro completo das decisões tomadas em assembleia, de forma organizada e transparente.',
    },
    {
      titulo: 'Abertura de Chamados',
      texto:
        'Relate problemas e solicite manutenção pelo app. Acompanhe o status da solicitação até a resolução.',
    },
    {
      titulo: 'Reserva de Áreas Comuns',
      texto:
        'Reserve salão de festas, churrasqueira ou quadra em poucos toques, com disponibilidade em tempo real.',
    },
    {
      titulo: 'Quadro Administrativo',
      texto:
        'Painel com informações financeiras, prestação de contas e comunicados da administração, tudo em um lugar.',
    },
  ];

  // breakpoints md (768px) e lg (1024px) do Tailwind
  private mqMd = window.matchMedia('(min-width: 768px)');
  private mqLg = window.matchMedia('(min-width: 1024px)');

  // Quantos cards ficam em destaque: 1 no mobile e lg e md.
  private calcularDestaques() {
    if (this.mqLg.matches) return 1; // lg: 1 card
    if (this.mqMd.matches) return 1; // md: 1 cards
    return 1; // mobile: 1 card
  }
  destaques = signal(this.calcularDestaques());

  // Mobile/lg md: 5 posições

  irParaCard(i: number) {
    this.irParaPosicao(Math.min(i, this.ultimaPosicao()));
  }
  private ultimaPosicao = computed(() => this.cards.length - this.destaques());

  indiceAtual = signal(0);
  private direcao = 1; // 1 = avançando, -1 = voltando
  private intervalo?: ReturnType<typeof setInterval>;

  private aoMudarTela = () => {
    this.destaques.set(this.calcularDestaques());
    if (this.indiceAtual() > this.ultimaPosicao()) {
      this.indiceAtual.set(this.ultimaPosicao());
    }
  };

  ngOnInit() {
    this.mqMd.addEventListener('change', this.aoMudarTela);
    this.mqLg.addEventListener('change', this.aoMudarTela);
    this.iniciarAutoplay();
  }

  ngOnDestroy() {
    this.mqMd.removeEventListener('change', this.aoMudarTela);
    this.mqLg.removeEventListener('change', this.aoMudarTela);
    this.pararAutoplay();
  }

  destacado(i: number) {
    return i >= this.indiceAtual() && i < this.indiceAtual() + this.destaques();
  }

  irParaPosicao(i: number) {
    this.indiceAtual.set(i);
    if (i === this.ultimaPosicao()) this.direcao = -1;
    if (i === 0) this.direcao = 1;
    this.iniciarAutoplay();
  }

  private proximo() {
    let prox = this.indiceAtual() + this.direcao;
    if (prox > this.ultimaPosicao() || prox < 0) {
      this.direcao *= -1;
      prox = this.indiceAtual() + this.direcao;
    }
    this.indiceAtual.set(prox);
  }

  private iniciarAutoplay() {
    this.pararAutoplay();
    this.intervalo = setInterval(() => this.proximo(), 5000);
  }

  private pararAutoplay() {
    if (this.intervalo) clearInterval(this.intervalo);
  }
}
