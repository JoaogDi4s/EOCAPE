package com.eocape.backend.unidade;

import com.eocape.backend.unidade.dto.UnidadeRequest;
import com.eocape.backend.unidade.dto.UnidadeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/unidade")
@RequiredArgsConstructor
public class UnidadeController {

    private final UnidadeService service;

    @PostMapping
    public ResponseEntity<UnidadeResponse> criar(@Valid @RequestBody UnidadeRequest request,
                                                 @AuthenticationPrincipal UUID solicitanteId) {
        UnidadeResponse salvo = service.criar(solicitanteId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping("/condominio/{condominioId}")
    public ResponseEntity<List<UnidadeResponse>> listar(@PathVariable UUID condominioId,
                                                        @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listar(solicitanteId, condominioId));
    }

    @GetMapping("/{id}")
    public UnidadeResponse buscarPorId(@PathVariable UUID id,
                                       @AuthenticationPrincipal UUID solicitanteId) {
        return service.buscarPorId(solicitanteId, id);
    }
}