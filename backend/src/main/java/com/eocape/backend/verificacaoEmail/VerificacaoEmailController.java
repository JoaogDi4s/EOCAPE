package com.eocape.backend.verificacaoEmail;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import com.eocape.backend.verificacaoEmail.dto.request.EnviarCodigoRequest;
import com.eocape.backend.verificacaoEmail.dto.request.VerificarCodigoRequest;
import com.eocape.backend.verificacaoEmail.dto.response.CodigoEnviadoResponse;
import com.eocape.backend.verificacaoEmail.dto.response.CodigoVerificadoResponse;

/**
 * Expõe os endpoints HTTP de verificações de e-mail e delega o processamento à camada de serviço.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/verificacao-email")
public class VerificacaoEmailController {

    /**
     * Serviço responsável por verificações de e-mail.
     */
    private final VerificacaoEmailService service;

    /**
     * Inicializa VerificacaoEmailController com os dados e as dependências necessários à operação.
     *
     * @param service Serviço responsável por verificações de e-mail.
     */
    public VerificacaoEmailController(VerificacaoEmailService service) {
        this.service = service;
    }

    /**
     * Cria e envia um código temporário, respeitando o intervalo entre reenvios.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws com.eocape.backend.common.exception.RegraNegocioException se ocorrer a condição: Aguarde um minuto antes de solicitar outro código.
     */
    @PostMapping("/codigo")
    public ResponseEntity<CodigoEnviadoResponse> enviarCodigo(
            @AuthenticationPrincipal UUID usuarioId,
            @Valid @RequestBody EnviarCodigoRequest request) {
        return ResponseEntity.ok(service.enviarCodigo(usuarioId, request));
    }

    /**
     * Valida o código de e-mail e emite a autorização para definir a senha.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws com.eocape.backend.common.exception.RegraNegocioException se ocorrer a condição: Este e-mail já está em uso.
     */
    @PostMapping("/verificar")
    public ResponseEntity<CodigoVerificadoResponse> verificarCodigo(
            @AuthenticationPrincipal UUID usuarioId,
            @Valid @RequestBody VerificarCodigoRequest request) {
        return ResponseEntity.ok(service.verificarCodigo(usuarioId, request));
    }
}