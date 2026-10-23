import { Component, OnDestroy, OnInit, computed, signal } from '@angular/core';

@Component({
  selector: 'app-benefits',
  templateUrl: './benefits.html',
})
export class Benefits implements OnInit, OnDestroy {
  cards = [
    {
      src: '/images/benefits/avisos-comunicados.png',
      alt: 'Homem segurando um megafone, representando a funcionalidade de avisos e comunicados do aplicativo.',
      titulo: 'Avisos e Comunicados',
      texto:
        'Receba comunicados oficiais do condomínio direto no celular, com histórico organizado para consultar quando quiser.',
    },
    {
      src: '/images/benefits/atas-de-assembleia.png',
      alt: 'Homem fazendo uma reunião, representando a funcionalidade de atas de assembleia do aplicativo.',
      titulo: 'Atas de Assembleia',
      texto:
        'Acesse o registro completo das decisões tomadas em assembleia, de forma organizada e transparente.',
    },
    {
      src: '/images/benefits/abertura-de-chamados.png',
      alt: 'Mulher pensando em soluções, representando a funcionalidade de abertura de chamados do aplicativo.',
      titulo: 'Abertura de Chamados',
      texto:
        'Relate problemas e solicite manutenção pelo app. Acompanhe o status da solicitação até a resolução.',
    },
    {
      src: '/images/benefits/reserva-de-areas-comuns.png',
      alt: 'Uma planilha com uma marcação de reserva, representando a funcionalidade de reserva de áreas comuns do aplicativo.',
      titulo: 'Reserva de Áreas Comuns',
      texto:
        'Reserve salão de festas, churrasqueira ou quadra em poucos toques, com disponibilidade em tempo real.',
    },
    {
      src: '/images/benefits/quadro-administrativo.png',
      alt: 'Homem analisando gráficos e planilhas, representando a funcionalidade de quadro administrativo do aplicativo.',
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
