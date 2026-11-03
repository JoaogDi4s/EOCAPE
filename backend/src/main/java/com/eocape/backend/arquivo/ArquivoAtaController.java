package com.eocape.backend.arquivo;

import com.eocape.backend.arquivo.dto.ArquivoAtaResponse;
import com.eocape.backend.arquivo.dto.VincularArquivoAtaRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ata/{ataId}/arquivo")
@RequiredArgsConstructor
public class ArquivoAtaController {

    private final ArquivoAtaService service;

    @PostMapping
    public ResponseEntity<ArquivoAtaResponse> vincular(@PathVariable UUID ataId,
            @Valid @RequestBody VincularArquivoAtaRequest request,
            @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.vincular(solicitanteId, ataId, request.arquivoId()));
    }

    @GetMapping
    public ResponseEntity<List<ArquivoAtaResponse>> listar(@PathVariable UUID ataId,
            @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listar(solicitanteId, ataId));
    }

    @DeleteMapping("/{vinculoId}")
    public ResponseEntity<Void> desvincular(@PathVariable UUID ataId,
            @PathVariable UUID vinculoId,
            @AuthenticationPrincipal UUID solicitanteId) {
        service.desvincular(solicitanteId, vinculoId);
        return ResponseEntity.noContent().build();
    }
}