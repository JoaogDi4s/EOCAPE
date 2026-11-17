package com.eocape.backend.common.exception;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
class GlobalExceptionHandlerTests {
    @Test void preservaMotivoDoConflitoDeCadastro() {
        var response = new GlobalExceptionHandler().statusExplicito(new ResponseStatusException(HttpStatus.CONFLICT,"CPF já cadastrado"));
        assertEquals(409,response.getStatusCode().value()); assertEquals("CPF já cadastrado",response.getBody().mensagem());
    }
    @Test void naoExpoeDetalheInternoDoServidor() {
        var response = new GlobalExceptionHandler().statusExplicito(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"SQL interno"));
        assertEquals("Internal Server Error",response.getBody().mensagem());
    }
}
