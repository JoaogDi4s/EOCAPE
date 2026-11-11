package com.eocape.backend.arquivo;

import com.eocape.backend.arquivo.dto.ArquivoResponse;
import com.eocape.backend.arquivo.dto.RegistrarArquivoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/arquivo")
@RequiredArgsConstructor
public class ArquivoController {

    private final ArquivoService service;

    @PostMapping
    public ResponseEntity<ArquivoResponse> registrar(@Valid @RequestBody RegistrarArquivoRequest request,
                                                     @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(solicitanteId, request));
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<ArquivoResponse> upload(@RequestParam UUID condominioId,
                                                  @RequestParam TipoArquivo tipo,
                                                  @RequestParam("arquivo") MultipartFile arquivo,
                                                  @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.upload(solicitanteId, condominioId, tipo, arquivo));
    }

    @GetMapping("/condominio/{condominioId}")
    public ResponseEntity<List<ArquivoResponse>> listar(@PathVariable UUID condominioId,
                                                        @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listar(solicitanteId, condominioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArquivoResponse> buscarPorId(@PathVariable UUID id,
                                                       @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.buscarPorId(solicitanteId, id));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Map<String, String>> download(@PathVariable UUID id,
                                                        @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(Map.of("url", service.gerarUrlDownload(solicitanteId, id)));
    }
}