package com.eocape.backend.espaco_comum;

import com.eocape.backend.espaco_comum.dto.EspacoComumRequest;
import com.eocape.backend.espaco_comum.dto.EspacoComumResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/espaco-comum")
@RequiredArgsConstructor
public class EspacoComumController {

    private final EspacoComumService service;

    @PostMapping
    public ResponseEntity<EspacoComumResponse.Detalhe> criar(@Valid @RequestBody EspacoComumRequest.Criar request) {
        EspacoComumResponse.Detalhe salvo = service.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public ResponseEntity<List<EspacoComumResponse.Detalhe>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public EspacoComumResponse.Detalhe buscarPorId(@PathVariable UUID id) {
        return service.buscarPorId(id);
    }
}