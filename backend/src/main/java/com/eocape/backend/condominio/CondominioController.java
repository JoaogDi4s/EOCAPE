package com.eocape.backend.condominio;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/condominio")
@RequiredArgsConstructor
public class CondominioController {

    private final CondominioService service;

    /** Condomínios em que o usuário logado tem vínculo ou papel ativo. */
    @GetMapping("/meus")
    public ResponseEntity<List<Condominio>> listarMeus(@AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listarMeus(solicitanteId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Condominio> buscarPorId(@PathVariable UUID id,
                                                  @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.buscarPorId(solicitanteId, id));
    }
}