package com.eocape.backend.arquivo;

import com.eocape.backend.arquivo.dto.ArquivoChamadoResponse;
import com.eocape.backend.arquivo.dto.VincularArquivoChamadoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Expõe os endpoints HTTP de anexos de chamados e delega o processamento à camada de serviço.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/chamado/{chamadoId}/arquivos")
@RequiredArgsConstructor
public class ArquivoChamadoController {

    /**
     * Serviço responsável por anexos de chamados.
     */
    private final ArquivoChamadoService service;

    /**
     * Associa o arquivo ou cadastro ao registro de anexos de chamados.
     *
     * @param chamadoId Identificador do chamado.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Arquivo não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Só o autor do chamado ou um gestor pode anexar arquivos.
     * @throws IllegalArgumentException se ocorrer a condição: O arquivo não pertence ao condomínio do chamado.
     * @throws IllegalStateException se ocorrer a condição: Este arquivo já está vinculado ao chamado.
     */
    @PostMapping
    public ResponseEntity<ArquivoChamadoResponse> vincular(@PathVariable UUID chamadoId,
                                                           @Valid @RequestBody VincularArquivoChamadoRequest request,
                                                           @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.vincular(solicitanteId, chamadoId, request.arquivoId()));
    }

    /**
     * Lista anexos de chamados.
     *
     * @param chamadoId Identificador do chamado.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Chamado não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Você só pode ver os anexos dos seus chamados.
     */
    @GetMapping
    public ResponseEntity<List<ArquivoChamadoResponse>> listar(@PathVariable UUID chamadoId,
                                                               @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listar(solicitanteId, chamadoId));
    }

    /**
     * Remove a associação do anexo de anexos de chamados.
     *
     * @param chamadoId Identificador do chamado.
     * @param vinculoId Identificador do vínculo do anexo com o registro.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Vínculo não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @DeleteMapping("/{vinculoId}")
    public ResponseEntity<Void> desvincular(@PathVariable UUID chamadoId,
                                            @PathVariable UUID vinculoId,
                                            @AuthenticationPrincipal UUID solicitanteId) {
        service.desvincular(solicitanteId, vinculoId);
        return ResponseEntity.noContent().build();
    }
}