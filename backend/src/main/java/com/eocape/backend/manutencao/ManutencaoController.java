package com.eocape.backend.manutencao;

import com.eocape.backend.manutencao.dto.CriarManutencaoRequest;
import com.eocape.backend.manutencao.dto.ManutencaoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/manutencao")
@RequiredArgsConstructor
public class ManutencaoController {

    private final ManutencaoService service;

    @PostMapping
    public ResponseEntity<ManutencaoResponse> criar(@Valid @RequestBody CriarManutencaoRequest request,
                                                    @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(solicitanteId, request));
    }

    @GetMapping("/condominio/{condominioId}")
    public ResponseEntity<List<ManutencaoResponse>> listar(@PathVariable UUID condominioId,
                                                           @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listar(solicitanteId, condominioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ManutencaoResponse> buscarPorId(@PathVariable UUID id,
                                                          @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.buscarPorId(solicitanteId, id));
    }
}