package com.eocape.backend.classificado;

import com.eocape.backend.classificado.dto.AlterarStatusClassificadoRequest;
import com.eocape.backend.classificado.dto.AtualizarClassificadoRequest;
import com.eocape.backend.classificado.dto.ClassificadoResponse;
import com.eocape.backend.classificado.dto.CriarClassificadoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/classificado")
@RequiredArgsConstructor
public class ClassificadoController {

    private final ClassificadoService service;

    @PostMapping
    public ResponseEntity<ClassificadoResponse> criar(@Valid @RequestBody CriarClassificadoRequest request,
                                                      @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(solicitanteId, request));
    }

    @GetMapping("/condominio/{condominioId}")
    public ResponseEntity<List<ClassificadoResponse>> listar(@PathVariable UUID condominioId,
                                                             @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listar(solicitanteId, condominioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassificadoResponse> buscarPorId(@PathVariable UUID id,
                                                            @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.buscarPorId(solicitanteId, id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ClassificadoResponse> atualizar(@PathVariable UUID id,
                                                          @Valid @RequestBody AtualizarClassificadoRequest request,
                                                          @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.atualizar(solicitanteId, id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ClassificadoResponse> alterarStatus(@PathVariable UUID id,
                                                              @Valid @RequestBody AlterarStatusClassificadoRequest request,
                                                              @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.alterarStatus(solicitanteId, id, request));
    }
}