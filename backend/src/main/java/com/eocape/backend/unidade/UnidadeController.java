package com.eocape.backend.unidade;

import com.eocape.backend.unidade.dto.UnidadeRequest;
import com.eocape.backend.unidade.dto.UnidadeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

import java.util.List;

@RestController
@RequestMapping("/unidade")
@RequiredArgsConstructor
public class UnidadeController {

    private final UnidadeService service;

    @PostMapping
    public ResponseEntity<UnidadeResponse> criar(@Valid @RequestBody UnidadeRequest request) {
        UnidadeResponse salvo = service.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public ResponseEntity<List<UnidadeResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public UnidadeResponse buscarPorId(@PathVariable UUID id) {
        return service.buscarPorId(id);
    }

}