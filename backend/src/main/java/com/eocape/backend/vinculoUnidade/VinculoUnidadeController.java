package com.eocape.backend.vinculoUnidade;

import com.eocape.backend.vinculoUnidade.dto.VinculoUnidadeRequest;
import com.eocape.backend.vinculoUnidade.dto.VinculoUnidadeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/vinculo-unidade")
@RequiredArgsConstructor
public class VinculoUnidadeController {

    private final VinculoUnidadeService service;

    @PostMapping
    public ResponseEntity<VinculoUnidadeResponse> criar(@Valid @RequestBody VinculoUnidadeRequest request) {
        VinculoUnidadeResponse salvo = service.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public ResponseEntity<List<VinculoUnidadeResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VinculoUnidadeResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}