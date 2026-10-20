package com.eocape.backend.area;

import com.eocape.backend.area.dto.AreaRequest;
import com.eocape.backend.area.dto.AreaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/area")
@RequiredArgsConstructor
public class AreaController {

    private final AreaService service;

    @PostMapping
    public ResponseEntity<AreaResponse> criar(@Valid @RequestBody AreaRequest request,
                                              @AuthenticationPrincipal UUID solicitanteId) {
        AreaResponse salvo = service.criar(solicitanteId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping("/condominio/{condominioId}")
    public ResponseEntity<List<AreaResponse>> listar(@PathVariable UUID condominioId,
                                                     @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listar(solicitanteId, condominioId));
    }

    @GetMapping("/{id}")
    public AreaResponse buscarPorId(@PathVariable UUID id,
                                    @AuthenticationPrincipal UUID solicitanteId) {
        return service.buscarPorId(solicitanteId, id);
    }
}