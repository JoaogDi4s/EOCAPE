package com.eocape.backend.manutencao;

import com.eocape.backend.manutencao.dto.CriarManutencaoRequest;
import com.eocape.backend.manutencao.dto.ManutencaoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Expõe os endpoints HTTP de manutenções e delega o processamento à camada de serviço.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/manutencao")
@RequiredArgsConstructor
public class ManutencaoController {

    /**
     * Serviço responsável por manutenções.
     */
    private final ManutencaoService service;

    /**
     * Cria manutenções.
     *
     * @param request Dados de entrada recebidos e validados para a operação.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Usuário não encontrado.
     * @throws IllegalArgumentException se ocorrer a condição: Informe a unidade ou a área, mas não as duas.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @PostMapping
    public ResponseEntity<ManutencaoResponse> criar(@Valid @RequestBody CriarManutencaoRequest request,
                                                    @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(solicitanteId, request));
    }

    /**
     * Lista manutenções.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @GetMapping("/condominio/{condominioId}")
    public ResponseEntity<List<ManutencaoResponse>> listar(@PathVariable UUID condominioId,
                                                           @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listar(solicitanteId, condominioId));
    }

    /**
     * Busca pelo identificador manutenções.
     *
     * @param id Identificador único do registro.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Manutenção não encontrada: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ManutencaoResponse> buscarPorId(@PathVariable UUID id,
                                                          @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.buscarPorId(solicitanteId, id));
    }
}