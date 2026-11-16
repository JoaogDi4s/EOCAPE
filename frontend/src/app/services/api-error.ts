import { HttpErrorResponse } from '@angular/common/http';
export function apiError(error: unknown, context: 'login' | 'request' = 'request'): string {
  if (error instanceof Error && error.name === 'TimeoutError')
    return 'O servidor demorou para responder. Tente novamente.';
  if (!(error instanceof HttpErrorResponse)) return 'Não foi possível concluir a operação.';
  if (error.status === 0 || error.status === 502 || error.status === 504)
    return 'Não conseguimos conectar agora. Confira sua conexão e tente novamente em instantes.';
  if (error.status === 401)
    return context === 'login'
      ? 'CPF ou senha não conferem. Revise os dados e tente novamente.'
      : 'Sua sessão expirou. Entre novamente para continuar.';
  if (error.status === 403)
    return 'Seu perfil não permite esta ação. Se precisar, fale com a administração.';
  if (error.status === 413) return 'Esse arquivo é grande demais. Escolha um arquivo de até 10 MB.';
  if (error.status === 429)
    return 'Foram feitas muitas tentativas. Aguarde um pouco antes de tentar novamente.';
  if (error.status === 400 && error.error?.campos && typeof error.error.campos === 'object') {
    const labels: Record<string, string> = {
      nome: 'Nome',
      cpf: 'CPF',
      email: 'E-mail',
      telefone: 'Telefone',
      unidadeId: 'Unidade',
      titulo: 'Título',
      descricao: 'Descrição',
      novaSenha: 'Nova senha',
      confirmacaoSenha: 'Confirmação da senha',
      bloco: 'Bloco',
      apartamento: 'Apartamento',
      inicio: 'Início',
      fim: 'Término',
      dataReuniao: 'Data da reunião',
    };
    const messages = Object.entries(error.error.campos)
      .filter(([, v]) => typeof v === 'string' && v.length < 200)
      .map(([key, value]) =>
        labels[key.split('.').pop() || '']
          ? labels[key.split('.').pop() || ''] + ': ' + value
          : String(value),
      );
    if (messages.length) return [...new Set(messages)].slice(0, 4).join(' · ');
  }
  if ([400, 404, 409].includes(error.status) && typeof error.error?.mensagem === 'string') {
    const message = error.error.mensagem.trim();
    if (message && message.length <= 500) return message;
  }
  if (error.status === 409)
    return 'Não foi possível salvar: há um conflito com os dados existentes.';
  if (error.status === 400) return 'Verifique os campos informados e tente novamente.';
  if (error.status === 404)
    return 'Este registro não está mais disponível. Atualize a lista para continuar.';
  return 'Não foi possível carregar ou salvar os dados. Tente novamente.';
}
