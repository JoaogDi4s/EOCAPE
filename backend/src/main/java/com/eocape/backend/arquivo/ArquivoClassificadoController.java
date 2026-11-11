package com.eocape.backend.arquivo;

import com.eocape.backend.arquivo.dto.ArquivoClassificadoResponse;
import com.eocape.backend.arquivo.dto.VincularArquivoClassificadoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/classificado/{classificadoId}/arquivos")
@RequiredArgsConstructor
public class ArquivoClassificadoController {

    private final ArquivoClassificadoService service;

    @PostMapping
    public ResponseEntity<ArquivoClassificadoResponse> vincular(
            @PathVariable UUID classificadoId,
            @Valid @RequestBody VincularArquivoClassificadoRequest request,
            @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.vincular(solicitanteId, classificadoId, request.arquivoId()));
    }

    @GetMapping
    public ResponseEntity<List<ArquivoClassificadoResponse>> listar(
            @PathVariable UUID classificadoId,
            @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listar(solicitanteId, classificadoId));
    }

    @DeleteMapping("/{vinculoId}")
    public ResponseEntity<Void> desvincular(@PathVariable UUID classificadoId,
                                            @PathVariable UUID vinculoId,
                                            @AuthenticationPrincipal UUID solicitanteId) {
        service.desvincular(solicitanteId, vinculoId);
        return ResponseEntity.noContent().build();
    }
}