import { Component, computed, signal } from '@angular/core';
import { whiteButton } from '../WhiteButton/whiteButton';

type Perfil = 'morador' | 'sindico';

interface Passo {
  titulo: string;
  texto: string;
}

@Component({
  selector: 'app-how-it-works',
  imports: [whiteButton],
  templateUrl: './how-it-works.html',
})
export class HowItWorks {
  perfis: { valor: Perfil; rotulo: string }[] = [
    { valor: 'morador', rotulo: 'Sou morador' },
    { valor: 'sindico', rotulo: 'Sou síndico' },
  ];

  private passosPorPerfil: Record<Perfil, Passo[]> = {
    morador: [
      {
        titulo: 'Contate seu síndico',
        texto:
          'Confirme com a administração do seu condomínio se sua unidade já está cadastrada no EOCAPE.',
      },
      {
        titulo: 'Acesse com seu CPF',
        texto:
          'Faça login com seu CPF e a senha padrão fornecida pelo condomínio (bloco + apartamento).',
      },
      {
        titulo: 'Confirme seu e-mail',
        texto:
          'No primeiro acesso, você recebe um código de verificação por e-mail para confirmar sua identidade.',
      },
      {
        titulo: 'Crie sua senha pessoal',
        texto:
          'Defina uma senha só sua. A partir daí, é só entrar com CPF e senha sempre que quiser.',
      },
      {
        titulo: 'Aproveite o EOCAPE',
        texto: 'Avisos, reservas, chamados e muito mais, tudo em um só lugar.',
      },
    ],
    sindico: [
      {
        titulo: 'Entre em contato com nossa equipe',
        texto: 'Fale com o time do EOCAPE para configurar o seu condomínio na plataforma.',
      },
      {
        titulo: 'Cadastre blocos e unidades',
        texto:
          'Registre os blocos e apartamentos do condomínio para gerar os acessos dos moradores.',
      },
      {
        titulo: 'Compartilhe os acessos',
        texto: 'Cada morador recebe uma senha padrão (bloco + apartamento) para o primeiro login.',
      },
      {
        titulo: 'Gerencie o quadro administrativo',
        texto: 'Publique avisos, atas de assembleia e acompanhe chamados de manutenção.',
      },
      {
        titulo: 'Simplifique sua gestão',
        texto: 'Menos burocracia no dia a dia e mais transparência com os moradores.',
      },
    ],
  };

  // começa em "síndico", como no seu protótipo
  perfil = signal<Perfil>('sindico');
  passos = computed(() => this.passosPorPerfil[this.perfil()]);

  escolher(p: Perfil) {
    this.perfil.set(p);
  }

  irParaContato() {
    document.getElementById('contato')?.scrollIntoView({ behavior: 'smooth' });
  }
}
