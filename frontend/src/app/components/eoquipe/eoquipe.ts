import { Component, OnDestroy, OnInit, signal } from '@angular/core';

interface TeamMember {
  name: string;
  role: string;
  description: string;
  image: string;
}

const TEAM_MEMBERS: TeamMember[] = [
  {
    name: 'Diego Dias',
    role: 'Gestor do Projeto, Scrum Master e Tester',
    description:
      'Responsável por planejar o projeto, acompanhar prazos e tarefas no Trello, conduzir as reuniões ágeis e testar as funcionalidades.',
    image: '/images/eoquipe/scrum_master.png',
  },
  {
    name: 'Rafaela Marins',
    role: 'Desenvolvedora Front-end, QA Analyst e Tester',
    description:
      'Responsável por implementar as telas a partir dos protótipos, definir critérios de qualidade e casos de teste e validar as funcionalidades.',
    image: '/images/eoquipe/QA_analyst.png',
  },
  {
    name: 'Vinícius Teixeira',
    role: 'Analista de BI, Analista de Suporte e Tester',
    description:
      'Responsável por analisar os dados do sistema, levantar as dificuldades dos usuários e testar as funcionalidades.',
    image: '/images/eoquipe/analista_BI.png',
  },
  {
    name: 'João Guilherme',
    role: 'Designer de UX/UI, Desenvolvedor Back-end, Product Owner e Tester',
    description:
      'Responsável por criar os protótipos e a identidade visual, implementar a lógica de negócio, priorizar as demandas do produto e testar o sistema.',
    image: '/images/eoquipe/UXUI_designer.png',
  },
  {
    name: 'Isabelle Aguiar',
    role: 'Gestora de Viabilidade e Tester',
    description:
      'Responsável por avaliar recursos, prazos e riscos do projeto, verificando se as funcionalidades propostas são viáveis, e por testar o sistema.',
    image: '/images/eoquipe/gestora_viabilidade.png',
  },
  {
    name: 'Francesco Farina',
    role: 'Especialista em Segurança e Tester',
    description:
      'Responsável por aplicar práticas de proteção aos dados dos usuários, prevenindo acessos indevidos, e por testar as funcionalidades.',
    image: '/images/eoquipe/especialista_seguranca.png',
  },
];

@Component({
  selector: 'app-eoquipe',
  styleUrl: './eoquipe.css',
  templateUrl: './eoquipe.html',
})
export class Eoquipe implements OnInit, OnDestroy {
  readonly members = TEAM_MEMBERS;
  readonly activeIndex = signal(0);
  private direction = 1;
  private interval?: ReturnType<typeof setInterval>;

  ngOnInit(): void {
    this.startAutoplay();
  }

  ngOnDestroy(): void {
    this.stopAutoplay();
  }

  selectMember(index: number): void {
    if (index >= 0 && index < this.members.length) {
      this.activeIndex.set(index);
      if (index === this.members.length - 1) this.direction = -1;
      if (index === 0) this.direction = 1;
      this.startAutoplay();
    }
  }

  private nextMember(): void {
    let next = this.activeIndex() + this.direction;
    if (next >= this.members.length || next < 0) {
      this.direction *= -1;
      next = this.activeIndex() + this.direction;
    }

    this.activeIndex.set(next);
  }

  private startAutoplay(): void {
    this.stopAutoplay();
    this.interval = setInterval(() => this.nextMember(), 5000);
  }

  private stopAutoplay(): void {
    if (this.interval) clearInterval(this.interval);
  }

  slideDirection(index: number): 'left' | 'right' | 'none' {
    if (index === this.members.length - 1) {
      return 'none';
    }

    return index % 2 === 0 ? 'right' : 'left';
  }
}
