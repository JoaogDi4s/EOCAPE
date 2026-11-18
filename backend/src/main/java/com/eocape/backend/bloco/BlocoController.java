package com.eocape.backend.bloco;

import com.eocape.backend.bloco.dto.BlocoRequest;
import com.eocape.backend.bloco.dto.BlocoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Expõe os endpoints HTTP de blocos e delega o processamento à camada de serviço.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/bloco")
@RequiredArgsConstructor
public class BlocoController {

    /**
     * Serviço responsável por blocos.
     */
    private final BlocoService blocoService;

    /**
     * Cria blocos.
     *
     * @param request Dados de entrada recebidos e validados para a operação.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Condomínio não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @PostMapping
    public ResponseEntity<BlocoResponse> criar(@Valid @RequestBody BlocoRequest request,
                                               @AuthenticationPrincipal UUID solicitanteId) {
        BlocoResponse response = blocoService.criar(solicitanteId, request);
        return ResponseEntity
                .created(URI.create("/bloco/" + response.id()))
                .body(response);
    }

    /**
     * Lista blocos.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return coleção dos registros encontrados pela consulta.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @GetMapping("/condominio/{condominioId}")
    public List<BlocoResponse> listar(@PathVariable UUID condominioId,
                                      @AuthenticationPrincipal UUID solicitanteId) {
        return blocoService.listar(solicitanteId, condominioId);
    }

    /**
     * Busca pelo identificador blocos.
     *
     * @param id Identificador único do registro.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return dados de bloco produzidos pela operação e destinados à resposta da API.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Bloco não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @GetMapping("/{id}")
    public BlocoResponse buscarPorId(@PathVariable UUID id,
                                     @AuthenticationPrincipal UUID solicitanteId) {
        return blocoService.buscarPorId(solicitanteId, id);
    }
}