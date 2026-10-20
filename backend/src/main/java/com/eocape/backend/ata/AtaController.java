package com.eocape.backend.ata;

import com.eocape.backend.ata.dto.AtaResponse;
import com.eocape.backend.ata.dto.CriarAtaRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/condominios/{condominioId}/atas")
@RequiredArgsConstructor
public class AtaController {

    private final AtaService ataService;

    @GetMapping
    public List<AtaResponse> listar(@PathVariable UUID condominioId,
                                    @AuthenticationPrincipal UUID usuarioId) {
        return ataService.listar(condominioId, usuarioId);
    }

    @GetMapping("/{ataId}")
    public AtaResponse buscar(@PathVariable UUID condominioId,
                              @PathVariable UUID ataId,
                              @AuthenticationPrincipal UUID usuarioId) {
        return ataService.buscar(condominioId, ataId, usuarioId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AtaResponse criar(@PathVariable UUID condominioId,
                             @Valid @RequestBody CriarAtaRequest request,
                             @AuthenticationPrincipal UUID usuarioId) {
        return ataService.criar(condominioId, usuarioId, request);
    }

    @PatchMapping("/{ataId}/publicar")
    public AtaResponse publicar(@PathVariable UUID condominioId,
                                @PathVariable UUID ataId,
                                @AuthenticationPrincipal UUID usuarioId) {
        return ataService.publicar(condominioId, ataId, usuarioId);
    }
}