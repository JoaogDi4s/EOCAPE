package com.eocape.backend.aviso;

import com.eocape.backend.aviso.dto.request.AvisoAtualizacaoRequest;
import com.eocape.backend.aviso.dto.request.AvisoRequest;
import com.eocape.backend.aviso.dto.response.AvisoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/condominio/{condominioId}/avisos")
@RequiredArgsConstructor
public class AvisoController {

    private final AvisoService avisoService;

    @PostMapping
    public ResponseEntity<AvisoResponse> criar(
            @PathVariable UUID condominioId,
            @Valid @RequestBody AvisoRequest request,
            Authentication authentication) {

        UUID autorId = UUID.fromString(authentication.getName());
        AvisoResponse response = avisoService.criar(condominioId, autorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public Page<AvisoResponse> listar(
            @PathVariable UUID condominioId,
            @PageableDefault(size = 20, sort = { "importante",
                    "criadoEm" }, direction = Sort.Direction.DESC) Pageable pageable) {

        return avisoService.listar(condominioId, pageable);
    }

    @GetMapping("/{avisoId}")
    public AvisoResponse buscar(@PathVariable UUID condominioId, @PathVariable UUID avisoId) {
        return avisoService.buscar(condominioId, avisoId);
    }

    @PutMapping("/{avisoId}")
    public AvisoResponse atualizar(
            @PathVariable UUID condominioId,
            @PathVariable UUID avisoId,
            @Valid @RequestBody AvisoAtualizacaoRequest request) {

        return avisoService.atualizar(condominioId, avisoId, request);
    }

    @DeleteMapping("/{avisoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable UUID condominioId, @PathVariable UUID avisoId) {
        avisoService.remover(condominioId, avisoId);
    }
}