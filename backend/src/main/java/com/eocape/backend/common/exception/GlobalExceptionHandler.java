package com.eocape.backend.common.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.Instant;

/**
 * Converte exceções da aplicação em respostas HTTP, preservando mensagens úteis e ocultando detalhes internos.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Representa os dados de erro validacao devolvidos pela aplicação.
     *
     * @author Equipe EOCAPE
     * @version 0.0.1-SNAPSHOT
     * @param status Estado atual do registro no seu fluxo de atendimento.
     * @param mensagem Mensagem descritiva da regra de negócio ou do resultado.
     * @param campos Mensagens de validação indexadas pelo nome dos campos inválidos.
     */
    public record ErroValidacao(int status, String mensagem, java.util.Map<String, String> campos) {}
    /**
     * Converte erros dos campos de entrada em uma resposta HTTP de validação.
     *
     * @param ex Exceção interceptada que será convertida em resposta HTTP.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroValidacao> validacao(MethodArgumentNotValidException ex) {
        java.util.Map<String, String> campos = new java.util.LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> campos.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(new ErroValidacao(400, "Revise os campos indicados para continuar.", campos));
    }
    /**
     * Converte o excesso de tamanho do upload em uma resposta HTTP 413.
     *
     * @param ex Exceção interceptada que será convertida em resposta HTTP.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErroResponse> arquivoGrande(MaxUploadSizeExceededException ex) {
        return resposta(HttpStatus.PAYLOAD_TOO_LARGE, "O arquivo ultrapassa o limite de 10 MB. Escolha um arquivo menor.");
    }
    /**
     * Preserva o status HTTP informado e oculta detalhes de falhas internas do servidor.
     *
     * @param ex Exceção interceptada que será convertida em resposta HTTP.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErroResponse> statusExplicito(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return resposta(status, status.is5xxServerError() || ex.getReason() == null
                ? status.getReasonPhrase() : ex.getReason());
    }

    /**
     * Representa os dados de erro devolvidos pela aplicação.
     *
     * @author Equipe EOCAPE
     * @version 0.0.1-SNAPSHOT
     * @param status Estado atual do registro no seu fluxo de atendimento.
     * @param erro Descrição do erro correspondente ao status HTTP.
     * @param mensagem Mensagem descritiva da regra de negócio ou do resultado.
     * @param timestamp Instante em que a resposta de erro foi produzida.
     */
    public record ErroResponse(int status, String erro, String mensagem, Instant timestamp) {
    }

    /**
     * Converte uma violação de regra de negócio em uma resposta HTTP 400.
     *
     * @param ex Exceção interceptada que será convertida em resposta HTTP.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     */
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> regraNegocio(RegraNegocioException ex) {
        return resposta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Converte a falta de autorização em uma resposta HTTP 403.
     *
     * @param ex Exceção interceptada que será convertida em resposta HTTP.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponse> acessoNegado(AccessDeniedException ex) {
        return resposta(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    /**
     * Representa a ausência do recurso solicitado no tratamento HTTP.
     *
     * @param ex Exceção interceptada que será convertida em resposta HTTP.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErroResponse> naoEncontrado(EntityNotFoundException ex) {
        return resposta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Converte um conflito de estado em uma resposta HTTP 409.
     *
     * @param ex Exceção interceptada que será convertida em resposta HTTP.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErroResponse> conflito(IllegalStateException ex) {
        return resposta(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Converte parâmetros inválidos em uma resposta HTTP 400.
     *
     * @param ex Exceção interceptada que será convertida em resposta HTTP.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResponse> requisicaoInvalida(IllegalArgumentException ex) {
        return resposta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Monta a resposta de erro com status, mensagem e instante de ocorrência.
     *
     * @param status Estado atual do registro no seu fluxo de atendimento.
     * @param mensagem Mensagem descritiva da regra de negócio ou do resultado.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     */
    private ResponseEntity<ErroResponse> resposta(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(new ErroResponse(
                status.value(),
                status.getReasonPhrase(),
                mensagem,
                Instant.now()));
    }
}
