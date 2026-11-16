import { HttpErrorResponse } from '@angular/common/http';
export function apiError(error: unknown): string {
  if (error instanceof Error && error.name === 'TimeoutError')
    return 'O servidor demorou para responder. Tente novamente.';
  if (!(error instanceof HttpErrorResponse)) return 'Não foi possível concluir a operação.';
  if (error.status === 0 || error.status === 502 || error.status === 504)
    return 'Não foi possível conectar ao servidor. Verifique se o backend está em execução.';
  if (error.status === 401) return 'CPF ou senha inválidos, ou sessão expirada. Entre novamente.';
  if (error.status === 403) return 'Você não tem permissão para esta operação.';
  if (error.status === 409)
    return 'Não foi possível salvar: há um conflito com os dados existentes.';
  if (error.status === 400) return 'Verifique os campos informados e tente novamente.';
  return 'Não foi possível carregar ou salvar os dados. Tente novamente.';
}
