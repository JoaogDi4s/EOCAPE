package com.eocape.backend.common.exception;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
/**
 * Verifica os cenários de global exception handler e as regras exercitadas pelos testes desta classe.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
class GlobalExceptionHandlerTests {
    /**
     * Verifica que preserva motivo do conflito de cadastro.
     *
     * @throws org.springframework.web.server.ResponseStatusException se ocorrer a condição: CPF já cadastrado.
     */
    @Test void preservaMotivoDoConflitoDeCadastro() {
        var response = new GlobalExceptionHandler().statusExplicito(new ResponseStatusException(HttpStatus.CONFLICT,"CPF já cadastrado"));
        assertEquals(409,response.getStatusCode().value()); assertEquals("CPF já cadastrado",response.getBody().mensagem());
    }
    /**
     * Verifica que nao expoe detalhe interno do servidor.
     *
     * @throws org.springframework.web.server.ResponseStatusException se ocorrer a condição: SQL interno.
     */
    @Test void naoExpoeDetalheInternoDoServidor() {
        var response = new GlobalExceptionHandler().statusExplicito(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"SQL interno"));
        assertEquals("Internal Server Error",response.getBody().mensagem());
    }
}
