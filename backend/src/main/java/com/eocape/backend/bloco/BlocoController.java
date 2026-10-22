package com.eocape.backend.bloco;

import com.eocape.backend.bloco.dto.BlocoRequest;
import com.eocape.backend.bloco.dto.BlocoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bloco")
@RequiredArgsConstructor
public class BlocoController {

    private final BlocoService blocoService;

    @PostMapping
    public ResponseEntity<BlocoResponse> criar(@Valid @RequestBody BlocoRequest request,
                                               @AuthenticationPrincipal UUID solicitanteId) {
        BlocoResponse response = blocoService.criar(solicitanteId, request);
        return ResponseEntity
                .created(URI.create("/bloco/" + response.id()))
                .body(response);
    }

    @GetMapping("/condominio/{condominioId}")
    public List<BlocoResponse> listar(@PathVariable UUID condominioId,
                                      @AuthenticationPrincipal UUID solicitanteId) {
        return blocoService.listar(solicitanteId, condominioId);
    }

    @GetMapping("/{id}")
    public BlocoResponse buscarPorId(@PathVariable UUID id,
                                     @AuthenticationPrincipal UUID solicitanteId) {
        return blocoService.buscarPorId(solicitanteId, id);
    }
}