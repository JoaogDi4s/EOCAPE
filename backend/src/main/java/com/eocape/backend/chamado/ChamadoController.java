package com.eocape.backend.chamado;

import com.eocape.backend.chamado.dto.AtualizarStatusChamadoRequest;
import com.eocape.backend.chamado.dto.ChamadoResponse;
import com.eocape.backend.chamado.dto.CriarChamadoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chamado")
@RequiredArgsConstructor
public class ChamadoController {

    private final ChamadoService service;

    @PostMapping
    public ResponseEntity<ChamadoResponse> criar(@Valid @RequestBody CriarChamadoRequest request,
                                                 @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(solicitanteId, request));
    }

    @GetMapping("/condominio/{condominioId}")
    public ResponseEntity<List<ChamadoResponse>> listar(@PathVariable UUID condominioId,
                                                        @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listar(solicitanteId, condominioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChamadoResponse> buscarPorId(@PathVariable UUID id,
                                                       @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.buscarPorId(solicitanteId, id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ChamadoResponse> atualizarStatus(@PathVariable UUID id,
                                                           @Valid @RequestBody AtualizarStatusChamadoRequest request,
                                                           @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.atualizarStatus(solicitanteId, id, request.status()));
    }
}