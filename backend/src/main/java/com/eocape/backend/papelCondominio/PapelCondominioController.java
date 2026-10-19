package com.eocape.backend.papelCondominio;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eocape.backend.papelCondominio.dto.PapelCondominioRequest;
import com.eocape.backend.papelCondominio.dto.PapelCondominioResponse;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/papel-condominio")
public class PapelCondominioController {

    private final PapelCondominioService service;

    public PapelCondominioController(PapelCondominioService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PapelCondominioResponse adicionar(@Valid @RequestBody PapelCondominioRequest request) {
        return service.adicionarPapel(request);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<PapelCondominioResponse> listarPorUsuario(@PathVariable UUID usuarioId) {
        return service.listarPapeisDoUsuario(usuarioId);
    }

    @GetMapping
    public ResponseEntity<List<PapelCondominioResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable UUID id) {
        service.desativarPapel(id);
    }
}