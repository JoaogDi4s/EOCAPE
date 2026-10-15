package com.eocape.backend.condominio;

import com.eocape.backend.condominio.dto.CondominioRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/condominio")
@RequiredArgsConstructor
public class CondominioController {

    private final CondominioService service;

    @PostMapping
    public ResponseEntity<Condominio> criar(@Valid @RequestBody CondominioRequest request) {
        Condominio salvo = service.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public ResponseEntity<List<Condominio>> listar() {
        return ResponseEntity.ok(service.listar());
    }
}