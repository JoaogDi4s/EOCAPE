package com.eocape.backend.vinculoUnidade;

import com.eocape.backend.vinculoUnidade.dto.VinculoUnidadeRequest;
import com.eocape.backend.vinculoUnidade.dto.VinculoUnidadeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Expõe os endpoints HTTP de vínculos entre usuários e unidades e delega o processamento à camada de serviço.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/vinculo-unidade")
@RequiredArgsConstructor
public class VinculoUnidadeController {

    /**
     * Serviço responsável por vínculos entre usuários e unidades.
     */
    private final VinculoUnidadeService service;

    /**
     * Cria vínculos entre usuários e unidades.
     *
     * @param request Dados de entrada recebidos e validados para a operação.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Unidade não encontrada: .
     * @throws IllegalArgumentException se ocorrer a condição: Tipo de vínculo inválido. Escolha proprietário, inquilino ou dependente..
     * @throws IllegalStateException se ocorrer a condição: Não foi possível salvar o vínculo: os dados violam uma regra do cadastro. Verifique se a unidade já está associada ao morador..
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @PostMapping
    public ResponseEntity<VinculoUnidadeResponse> criar(@Valid @RequestBody VinculoUnidadeRequest request,
            @AuthenticationPrincipal UUID solicitanteId) {
        VinculoUnidadeResponse salvo = service.criar(solicitanteId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    /**
     * Lista os registros vinculados ao usuário autenticado de vínculos entre usuários e unidades.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     */
    @GetMapping("/meus")
    public ResponseEntity<List<VinculoUnidadeResponse>> listarMeus(@AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listarMeus(solicitanteId));
    }

    /**
     * Lista os registros do condomínio de vínculos entre usuários e unidades.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @GetMapping("/condominio/{condominioId}")
    public ResponseEntity<List<VinculoUnidadeResponse>> listarDoCondominio(
            @PathVariable UUID condominioId,
            @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listarDoCondominio(solicitanteId, condominioId));
    }

    /**
     * Busca pelo identificador vínculos entre usuários e unidades.
     *
     * @param id Identificador único do registro.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Vínculo não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VinculoUnidadeResponse> buscarPorId(@PathVariable UUID id,
            @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.buscarPorId(solicitanteId, id));
    }
}