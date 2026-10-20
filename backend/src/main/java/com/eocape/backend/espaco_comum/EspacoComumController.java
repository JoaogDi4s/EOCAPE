package com.eocape.backend.espaco_comum;

import com.eocape.backend.espaco_comum.dto.EspacoComumRequest;
import com.eocape.backend.espaco_comum.dto.EspacoComumResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/espaco-comum")
@RequiredArgsConstructor
public class EspacoComumController {

    private final EspacoComumService service;

    @PostMapping
    public ResponseEntity<EspacoComumResponse.Detalhe> criar(@Valid @RequestBody EspacoComumRequest.Criar request,
                                                             @AuthenticationPrincipal UUID solicitanteId) {
        EspacoComumResponse.Detalhe salvo = service.criar(solicitanteId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping("/condominio/{condominioId}")
    public ResponseEntity<List<EspacoComumResponse.Detalhe>> listar(@PathVariable UUID condominioId,
                                                                    @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listar(solicitanteId, condominioId));
    }

    @GetMapping("/{id}")
    public EspacoComumResponse.Detalhe buscarPorId(@PathVariable UUID id,
                                                   @AuthenticationPrincipal UUID solicitanteId) {
        return service.buscarPorId(solicitanteId, id);
    }
}