package com.eocape.backend.arquivo;

import com.eocape.backend.arquivo.dto.ArquivoAtaResponse;
import com.eocape.backend.arquivo.dto.VincularArquivoAtaRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Expõe os endpoints HTTP de anexos de atas e delega o processamento à camada de serviço.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/ata/{ataId}/arquivo")
@RequiredArgsConstructor
public class ArquivoAtaController {

    /**
     * Serviço responsável por anexos de atas.
     */
    private final ArquivoAtaService service;

    /**
     * Associa o arquivo ou cadastro ao registro de anexos de atas.
     *
     * @param ataId Identificador da ata de assembleia.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Arquivo não encontrado: .
     * @throws IllegalArgumentException se ocorrer a condição: O arquivo não pertence ao condomínio da ata.
     * @throws IllegalStateException se ocorrer a condição: Este arquivo já está vinculado à ata.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @PostMapping
    public ResponseEntity<ArquivoAtaResponse> vincular(@PathVariable UUID ataId,
            @Valid @RequestBody VincularArquivoAtaRequest request,
            @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.vincular(solicitanteId, ataId, request.arquivoId()));
    }

    /**
     * Lista anexos de atas.
     *
     * @param ataId Identificador da ata de assembleia.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Ata não encontrada: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @GetMapping
    public ResponseEntity<List<ArquivoAtaResponse>> listar(@PathVariable UUID ataId,
            @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listar(solicitanteId, ataId));
    }

    /**
     * Remove a associação do anexo de anexos de atas.
     *
     * @param ataId Identificador da ata de assembleia.
     * @param vinculoId Identificador do vínculo do anexo com o registro.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Vínculo não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @DeleteMapping("/{vinculoId}")
    public ResponseEntity<Void> desvincular(@PathVariable UUID ataId,
            @PathVariable UUID vinculoId,
            @AuthenticationPrincipal UUID solicitanteId) {
        service.desvincular(solicitanteId, vinculoId);
        return ResponseEntity.noContent().build();
    }
}