package com.eocape.backend.unidade;

import com.eocape.backend.unidade.dto.UnidadeRequest;
import com.eocape.backend.unidade.dto.UnidadeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Expõe os endpoints HTTP de unidades e delega o processamento à camada de serviço.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/unidade")
@RequiredArgsConstructor
public class UnidadeController {

    /**
     * Serviço responsável por unidades.
     */
    private final UnidadeService service;

    /**
     * Cria unidades.
     *
     * @param request Dados de entrada recebidos e validados para a operação.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Bloco não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @PostMapping
    public ResponseEntity<UnidadeResponse> criar(@Valid @RequestBody UnidadeRequest request,
                                                 @AuthenticationPrincipal UUID solicitanteId) {
        UnidadeResponse salvo = service.criar(solicitanteId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    /**
     * Lista unidades.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @GetMapping("/condominio/{condominioId}")
    public ResponseEntity<List<UnidadeResponse>> listar(@PathVariable UUID condominioId,
                                                        @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listar(solicitanteId, condominioId));
    }

    /**
     * Busca pelo identificador unidades.
     *
     * @param id Identificador único do registro.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return dados de unidade produzidos pela operação e destinados à resposta da API.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Unidade não encontrada: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @GetMapping("/{id}")
    public UnidadeResponse buscarPorId(@PathVariable UUID id,
                                       @AuthenticationPrincipal UUID solicitanteId) {
        return service.buscarPorId(solicitanteId, id);
    }
}