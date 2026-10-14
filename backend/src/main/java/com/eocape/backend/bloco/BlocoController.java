package com.eocape.backend.bloco;

import com.eocape.backend.bloco.dto.BlocoRequest;
import com.eocape.backend.bloco.dto.BlocoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<BlocoResponse> criar(@Valid @RequestBody BlocoRequest request) {
        BlocoResponse response = blocoService.criar(request);
        return ResponseEntity
                .created(URI.create("/bloco/" + response.id()))
                .body(response);
    }

    @GetMapping
    public List<BlocoResponse> listar() {
        return blocoService.listar();
    }

    @GetMapping("/{id}")
    public BlocoResponse buscarPorId(@PathVariable UUID id) {
        return blocoService.buscarPorId(id);
    }
}