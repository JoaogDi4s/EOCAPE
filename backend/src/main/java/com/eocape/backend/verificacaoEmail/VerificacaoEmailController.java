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

@RestController
@RequestMapping("/verificacao-email")
public class VerificacaoEmailController {

    private final VerificacaoEmailService service;

    public VerificacaoEmailController(VerificacaoEmailService service) {
        this.service = service;
    }

    @PostMapping("/codigo")
    public ResponseEntity<CodigoEnviadoResponse> enviarCodigo(
            @AuthenticationPrincipal UUID usuarioId,
            @Valid @RequestBody EnviarCodigoRequest request) {
        return ResponseEntity.ok(service.enviarCodigo(usuarioId, request));
    }

    @PostMapping("/verificar")
    public ResponseEntity<CodigoVerificadoResponse> verificarCodigo(
            @AuthenticationPrincipal UUID usuarioId,
            @Valid @RequestBody VerificarCodigoRequest request) {
        return ResponseEntity.ok(service.verificarCodigo(usuarioId, request));
    }
}