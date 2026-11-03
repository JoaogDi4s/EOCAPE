package com.eocape.backend.arquivo;

import com.eocape.backend.arquivo.dto.ArquivoChamadoResponse;
import com.eocape.backend.arquivo.dto.VincularArquivoChamadoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chamado/{chamadoId}/arquivos")
@RequiredArgsConstructor
public class ArquivoChamadoController {

    private final ArquivoChamadoService service;

    @PostMapping
    public ResponseEntity<ArquivoChamadoResponse> vincular(@PathVariable UUID chamadoId,
                                                           @Valid @RequestBody VincularArquivoChamadoRequest request,
                                                           @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.vincular(solicitanteId, chamadoId, request.arquivoId()));
    }

    @GetMapping
    public ResponseEntity<List<ArquivoChamadoResponse>> listar(@PathVariable UUID chamadoId,
                                                               @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listar(solicitanteId, chamadoId));
    }

    @DeleteMapping("/{vinculoId}")
    public ResponseEntity<Void> desvincular(@PathVariable UUID chamadoId,
                                            @PathVariable UUID vinculoId,
                                            @AuthenticationPrincipal UUID solicitanteId) {
        service.desvincular(solicitanteId, vinculoId);
        return ResponseEntity.noContent().build();
    }
}