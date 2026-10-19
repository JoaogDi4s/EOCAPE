package com.eocape.backend.verificacaoEmail;

import com.eocape.backend.verificacaoEmail.dto.request.EnviarCodigoRequest;
import com.eocape.backend.verificacaoEmail.dto.request.VerificarCodigoRequest;
import com.eocape.backend.verificacaoEmail.dto.response.CodigoEnviadoResponse;
import com.eocape.backend.verificacaoEmail.dto.response.CodigoVerificadoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/verificacao-email")
@RequiredArgsConstructor
public class VerificacaoEmailController {

    private final VerificacaoEmailService service;

    // usuarioId: placeholder. Ajuste conforme o token emitido no login por CPF.
    @PostMapping("/codigo")
    public ResponseEntity<CodigoEnviadoResponse> enviarCodigo(
            @RequestHeader("X-Usuario-Id") UUID usuarioId,
            @Valid @RequestBody EnviarCodigoRequest request) {
        return ResponseEntity.ok(service.enviarCodigo(usuarioId, request));
    }

    @PostMapping("/verificar")
    public ResponseEntity<CodigoVerificadoResponse> verificarCodigo(
            @Valid @RequestBody VerificarCodigoRequest request) {
        return ResponseEntity.ok(service.verificarCodigo(request));
    }
}