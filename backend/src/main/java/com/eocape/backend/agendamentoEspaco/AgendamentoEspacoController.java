package com.eocape.backend.agendamentoEspaco;

import com.eocape.backend.agendamentoEspaco.dto.AgendamentoEspacoResponse;
import com.eocape.backend.agendamentoEspaco.dto.CriarAgendamentoEspacoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/agendamento-espaco")
@RequiredArgsConstructor
public class AgendamentoEspacoController {

    private final AgendamentoEspacoService service;

    @PostMapping
    public ResponseEntity<AgendamentoEspacoResponse> criar(@Valid @RequestBody CriarAgendamentoEspacoRequest request,
                                                           @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(solicitanteId, request));
    }

    @GetMapping("/condominio/{condominioId}")
    public ResponseEntity<List<AgendamentoEspacoResponse>> listar(@PathVariable UUID condominioId,
                                                                  @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listar(solicitanteId, condominioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoEspacoResponse> buscarPorId(@PathVariable UUID id,
                                                                 @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.buscarPorId(solicitanteId, id));
    }

    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<AgendamentoEspacoResponse> aprovar(@PathVariable UUID id,
                                                             @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.aprovar(solicitanteId, id));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<AgendamentoEspacoResponse> cancelar(@PathVariable UUID id,
                                                              @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.cancelar(solicitanteId, id));
    }
}