import { HttpErrorResponse } from '@angular/common/http';
import { apiError } from './api-error';
describe('API error messages', () => {
  it('explains field validation and file size errors', () => {
    expect(
      apiError(
        new HttpErrorResponse({
          status: 400,
          error: { campos: { 'usuario.email': 'E-mail inválido' } },
        }),
      ),
    ).toContain('E-mail: E-mail inválido');
    expect(apiError(new HttpErrorResponse({ status: 413 }))).toContain('10 MB');
  });
  it('preserves the specific conflict returned by the backend', () => {
    expect(
      apiError(new HttpErrorResponse({ status: 409, error: { mensagem: 'E-mail já cadastrado' } })),
    ).toBe('E-mail já cadastrado');
  });
  it('keeps a safe fallback for unstructured errors', () => {
    expect(apiError(new HttpErrorResponse({ status: 409, error: 'SQL detail' }))).toContain(
      'conflito',
    );
    expect(
      apiError(
        new HttpErrorResponse({ status: 500, error: { mensagem: 'Internal database details' } }),
      ),
    ).not.toContain('database');
  });
});
