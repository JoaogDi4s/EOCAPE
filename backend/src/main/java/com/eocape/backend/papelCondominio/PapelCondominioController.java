package com.eocape.backend.papelCondominio;

import com.eocape.backend.papelCondominio.dto.PapelCondominioRequest;
import com.eocape.backend.papelCondominio.dto.PapelCondominioResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/papel-condominio")
@RequiredArgsConstructor
public class PapelCondominioController {

    private final PapelCondominioService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PapelCondominioResponse adicionar(@Valid @RequestBody PapelCondominioRequest request,
                                             @AuthenticationPrincipal UUID solicitanteId) {
        return service.adicionarPapel(solicitanteId, request);
    }

    @GetMapping("/meus")
    public ResponseEntity<List<PapelCondominioResponse>> listarMeus(@AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listarMeusPapeis(solicitanteId));
    }

    @GetMapping("/condominio/{condominioId}")
    public ResponseEntity<List<PapelCondominioResponse>> listarDoCondominio(
            @PathVariable UUID condominioId,
            @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listarDoCondominio(solicitanteId, condominioId));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable UUID id,
                          @AuthenticationPrincipal UUID solicitanteId) {
        service.desativarPapel(solicitanteId, id);
    }
}