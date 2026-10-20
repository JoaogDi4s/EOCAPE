package com.eocape.backend.vinculoUnidade;

import com.eocape.backend.vinculoUnidade.dto.VinculoUnidadeRequest;
import com.eocape.backend.vinculoUnidade.dto.VinculoUnidadeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/vinculo-unidade")
@RequiredArgsConstructor
public class VinculoUnidadeController {

    private final VinculoUnidadeService service;

    @PostMapping
    public ResponseEntity<VinculoUnidadeResponse> criar(@Valid @RequestBody VinculoUnidadeRequest request,
            @AuthenticationPrincipal UUID solicitanteId) {
        VinculoUnidadeResponse salvo = service.criar(solicitanteId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping("/meus")
    public ResponseEntity<List<VinculoUnidadeResponse>> listarMeus(@AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listarMeus(solicitanteId));
    }

    @GetMapping("/condominio/{condominioId}")
    public ResponseEntity<List<VinculoUnidadeResponse>> listarDoCondominio(
            @PathVariable UUID condominioId,
            @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listarDoCondominio(solicitanteId, condominioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VinculoUnidadeResponse> buscarPorId(@PathVariable UUID id,
            @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.buscarPorId(solicitanteId, id));
    }
}