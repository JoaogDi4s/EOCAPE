package com.eocape.backend.arquivo;

import com.eocape.backend.arquivo.dto.ArquivoManutencaoResponse;
import com.eocape.backend.arquivo.dto.VincularArquivoManutencaoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Expõe os endpoints HTTP de anexos de manutenções e delega o processamento à camada de serviço.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/manutencao/{manutencaoId}/arquivos")
@RequiredArgsConstructor
public class ArquivoManutencaoController {

    /**
     * Serviço responsável por anexos de manutenções.
     */
    private final ArquivoManutencaoService service;

    /**
     * Associa o arquivo ou cadastro ao registro de anexos de manutenções.
     *
     * @param manutencaoId Identificador da manutenção.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Arquivo não encontrado: .
     * @throws IllegalArgumentException se ocorrer a condição: O arquivo não pertence ao condomínio da manutenção.
     * @throws IllegalStateException se ocorrer a condição: Este arquivo já está vinculado à manutenção.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @PostMapping
    public ResponseEntity<ArquivoManutencaoResponse> vincular(
            @PathVariable UUID manutencaoId,
            @Valid @RequestBody VincularArquivoManutencaoRequest request,
            @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.vincular(solicitanteId, manutencaoId, request.arquivoId()));
    }

    /**
     * Lista anexos de manutenções.
     *
     * @param manutencaoId Identificador da manutenção.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Manutenção não encontrada: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @GetMapping
    public ResponseEntity<List<ArquivoManutencaoResponse>> listar(
            @PathVariable UUID manutencaoId,
            @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listar(solicitanteId, manutencaoId));
    }

    /**
     * Remove a associação do anexo de anexos de manutenções.
     *
     * @param manutencaoId Identificador da manutenção.
     * @param vinculoId Identificador do vínculo do anexo com o registro.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Vínculo não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @DeleteMapping("/{vinculoId}")
    public ResponseEntity<Void> desvincular(@PathVariable UUID manutencaoId,
                                            @PathVariable UUID vinculoId,
                                            @AuthenticationPrincipal UUID solicitanteId) {
        service.desvincular(solicitanteId, vinculoId);
        return ResponseEntity.noContent().build();
    }
}