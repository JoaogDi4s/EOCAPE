package com.eocape.backend.arquivo;

import com.eocape.backend.arquivo.dto.ArquivoManutencaoResponse;
import com.eocape.backend.arquivo.dto.VincularArquivoManutencaoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/manutencao/{manutencaoId}/arquivos")
@RequiredArgsConstructor
public class ArquivoManutencaoController {

    private final ArquivoManutencaoService service;

    @PostMapping
    public ResponseEntity<ArquivoManutencaoResponse> vincular(
            @PathVariable UUID manutencaoId,
            @Valid @RequestBody VincularArquivoManutencaoRequest request,
            @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.vincular(solicitanteId, manutencaoId, request.arquivoId()));
    }

    @GetMapping
    public ResponseEntity<List<ArquivoManutencaoResponse>> listar(
            @PathVariable UUID manutencaoId,
            @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listar(solicitanteId, manutencaoId));
    }

    @DeleteMapping("/{vinculoId}")
    public ResponseEntity<Void> desvincular(@PathVariable UUID manutencaoId,
                                            @PathVariable UUID vinculoId,
                                            @AuthenticationPrincipal UUID solicitanteId) {
        service.desvincular(solicitanteId, vinculoId);
        return ResponseEntity.noContent().build();
    }
}