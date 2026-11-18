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

/**
 * Expõe os endpoints HTTP de reservas de espaços comuns e delega o processamento à camada de serviço.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/agendamento-espaco")
@RequiredArgsConstructor
public class AgendamentoEspacoController {

    /**
     * Serviço responsável por reservas de espaços comuns.
     */
    private final AgendamentoEspacoService service;

    /**
     * Cria reservas de espaços comuns.
     *
     * @param request Dados de entrada recebidos e validados para a operação.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Usuário não encontrado.
     * @throws IllegalStateException se ocorrer a condição: Horário já reservado para este espaço.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     * @throws IllegalArgumentException se ocorrer a condição: A reserva deve estar dentro do horário de funcionamento: ;  às .
     */
    @PostMapping
    public ResponseEntity<AgendamentoEspacoResponse> criar(@Valid @RequestBody CriarAgendamentoEspacoRequest request,
                                                           @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(solicitanteId, request));
    }

    /**
     * Lista reservas de espaços comuns.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @GetMapping("/condominio/{condominioId}")
    public ResponseEntity<List<AgendamentoEspacoResponse>> listar(@PathVariable UUID condominioId,
                                                                  @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listar(solicitanteId, condominioId));
    }

    /**
     * Busca pelo identificador reservas de espaços comuns.
     *
     * @param id Identificador único do registro.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Você só pode visualizar as suas reservas.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Agendamento não encontrado: .
     */
    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoEspacoResponse> buscarPorId(@PathVariable UUID id,
                                                                 @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.buscarPorId(solicitanteId, id));
    }

    /**
     * Aprova a reserva pendente de reservas de espaços comuns.
     *
     * @param id Identificador único do registro.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws IllegalStateException se ocorrer a condição: Só é possível aprovar reservas pendentes.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Agendamento não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<AgendamentoEspacoResponse> aprovar(@PathVariable UUID id,
                                                             @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.aprovar(solicitanteId, id));
    }

    /**
     * Cancela reservas de espaços comuns.
     *
     * @param id Identificador único do registro.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws IllegalStateException se ocorrer a condição: Esta reserva não pode mais ser cancelada.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Agendamento não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<AgendamentoEspacoResponse> cancelar(@PathVariable UUID id,
                                                              @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.cancelar(solicitanteId, id));
    }
}