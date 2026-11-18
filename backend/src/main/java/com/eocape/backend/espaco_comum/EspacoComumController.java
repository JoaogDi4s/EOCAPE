package com.eocape.backend.espaco_comum;

import com.eocape.backend.espaco_comum.dto.EspacoComumRequest;
import com.eocape.backend.espaco_comum.dto.EspacoComumResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Expõe os endpoints HTTP de espaços comuns e delega o processamento à camada de serviço.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/espaco-comum")
@RequiredArgsConstructor
public class EspacoComumController {

    /**
     * Serviço responsável por espaços comuns.
     */
    private final EspacoComumService service;

    /**
     * Cria espaços comuns.
     *
     * @param request Dados de entrada recebidos e validados para a operação.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Área não encontrada: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @PostMapping
    public ResponseEntity<EspacoComumResponse.Detalhe> criar(@Valid @RequestBody EspacoComumRequest.Criar request,
                                                             @AuthenticationPrincipal UUID solicitanteId) {
        EspacoComumResponse.Detalhe salvo = service.criar(solicitanteId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    /**
     * Lista espaços comuns.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @GetMapping("/condominio/{condominioId}")
    public ResponseEntity<List<EspacoComumResponse.Detalhe>> listar(@PathVariable UUID condominioId,
                                                                    @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listar(solicitanteId, condominioId));
    }

    /**
     * Busca pelo identificador espaços comuns.
     *
     * @param id Identificador único do registro.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resultado da operação no tipo EspacoComumResponse.Detalhe.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Espaço comum não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @GetMapping("/{id}")
    public EspacoComumResponse.Detalhe buscarPorId(@PathVariable UUID id,
                                                   @AuthenticationPrincipal UUID solicitanteId) {
        return service.buscarPorId(solicitanteId, id);
    }
}