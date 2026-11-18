package com.eocape.backend.arquivo;

import com.eocape.backend.arquivo.dto.ArquivoClassificadoResponse;
import com.eocape.backend.arquivo.dto.VincularArquivoClassificadoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Expõe os endpoints HTTP de anexos de classificados e delega o processamento à camada de serviço.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/classificado/{classificadoId}/arquivos")
@RequiredArgsConstructor
public class ArquivoClassificadoController {

    /**
     * Serviço responsável por anexos de classificados.
     */
    private final ArquivoClassificadoService service;

    /**
     * Associa o arquivo ou cadastro ao registro de anexos de classificados.
     *
     * @param classificadoId Identificador do anúncio classificado.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Arquivo não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Só o dono do anúncio ou um gestor pode anexar arquivos.
     * @throws IllegalArgumentException se ocorrer a condição: O arquivo não pertence ao condomínio do anúncio.
     * @throws IllegalStateException se ocorrer a condição: Este arquivo já está vinculado ao anúncio.
     */
    @PostMapping
    public ResponseEntity<ArquivoClassificadoResponse> vincular(
            @PathVariable UUID classificadoId,
            @Valid @RequestBody VincularArquivoClassificadoRequest request,
            @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.vincular(solicitanteId, classificadoId, request.arquivoId()));
    }

    /**
     * Lista anexos de classificados.
     *
     * @param classificadoId Identificador do anúncio classificado.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Anúncio não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Este anúncio não está disponível.
     */
    @GetMapping
    public ResponseEntity<List<ArquivoClassificadoResponse>> listar(
            @PathVariable UUID classificadoId,
            @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listar(solicitanteId, classificadoId));
    }

    /**
     * Remove a associação do anexo de anexos de classificados.
     *
     * @param classificadoId Identificador do anúncio classificado.
     * @param vinculoId Identificador do vínculo do anexo com o registro.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Vínculo não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @DeleteMapping("/{vinculoId}")
    public ResponseEntity<Void> desvincular(@PathVariable UUID classificadoId,
                                            @PathVariable UUID vinculoId,
                                            @AuthenticationPrincipal UUID solicitanteId) {
        service.desvincular(solicitanteId, vinculoId);
        return ResponseEntity.noContent().build();
    }
}