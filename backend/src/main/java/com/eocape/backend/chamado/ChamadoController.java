package com.eocape.backend.chamado;

import com.eocape.backend.chamado.dto.AtualizarStatusChamadoRequest;
import com.eocape.backend.chamado.dto.ChamadoResponse;
import com.eocape.backend.chamado.dto.CriarChamadoRequest;
import com.eocape.backend.chamado.dto.HistoricoChamadoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Expõe os endpoints HTTP de chamados de manutenção e delega o processamento à camada de serviço.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/chamado")
@RequiredArgsConstructor
public class ChamadoController {

    /**
     * Serviço responsável por chamados de manutenção.
     */
    private final ChamadoService service;

    /**
     * Cria chamados de manutenção.
     *
     * @param request Dados de entrada recebidos e validados para a operação.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Área não encontrada: .
     * @throws IllegalArgumentException se ocorrer a condição: A área não pertence ao condomínio da unidade.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Você só pode abrir chamados para a sua unidade.
     */
    @PostMapping
    public ResponseEntity<ChamadoResponse> criar(@Valid @RequestBody CriarChamadoRequest request,
                                                 @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(solicitanteId, request));
    }

    /**
     * Lista chamados de manutenção.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @GetMapping("/condominio/{condominioId}")
    public ResponseEntity<List<ChamadoResponse>> listar(@PathVariable UUID condominioId,
                                                        @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listar(solicitanteId, condominioId));
    }

    /**
     * Busca pelo identificador chamados de manutenção.
     *
     * @param id Identificador único do registro.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Você só pode visualizar os seus chamados.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Chamado não encontrado: .
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChamadoResponse> buscarPorId(@PathVariable UUID id,
                                                       @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.buscarPorId(solicitanteId, id));
    }

    /**
     * Atualiza o status e o histórico de chamados de manutenção.
     *
     * @param id Identificador único do registro.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws IllegalStateException se ocorrer a condição: O chamado já está com este status.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Chamado não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ChamadoResponse> atualizarStatus(@PathVariable UUID id,
                                                           @Valid @RequestBody AtualizarStatusChamadoRequest request,
                                                           @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.atualizarStatus(
                solicitanteId, id, request.status(), request.comentario()));
    }

    /**
     * Consulta o histórico de alterações de chamados de manutenção.
     *
     * @param id Identificador único do registro.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Você só pode visualizar os seus chamados.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Chamado não encontrado: .
     */
    @GetMapping("/{id}/historico")
    public ResponseEntity<List<HistoricoChamadoResponse>> historico(@PathVariable UUID id,
                                                                    @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.historico(solicitanteId, id));
    }
}