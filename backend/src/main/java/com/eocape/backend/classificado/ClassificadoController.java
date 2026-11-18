package com.eocape.backend.classificado;

import com.eocape.backend.classificado.dto.AlterarStatusClassificadoRequest;
import com.eocape.backend.classificado.dto.AtualizarClassificadoRequest;
import com.eocape.backend.classificado.dto.ClassificadoResponse;
import com.eocape.backend.classificado.dto.CriarClassificadoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Expõe os endpoints HTTP de classificados e delega o processamento à camada de serviço.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/classificado")
@RequiredArgsConstructor
public class ClassificadoController {

    /**
     * Serviço responsável por classificados.
     */
    private final ClassificadoService service;

    /**
     * Cria classificados.
     *
     * @param request Dados de entrada recebidos e validados para a operação.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Usuário não encontrado.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     * @throws IllegalArgumentException se ocorrer a condição: Doação não pode ter preço.
     */
    @PostMapping
    public ResponseEntity<ClassificadoResponse> criar(@Valid @RequestBody CriarClassificadoRequest request,
                                                      @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(solicitanteId, request));
    }

    /**
     * Lista classificados.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @GetMapping("/condominio/{condominioId}")
    public ResponseEntity<List<ClassificadoResponse>> listar(@PathVariable UUID condominioId,
                                                             @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listar(solicitanteId, condominioId));
    }

    /**
     * Busca pelo identificador classificados.
     *
     * @param id Identificador único do registro.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Este anúncio não está disponível.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Anúncio não encontrado: .
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClassificadoResponse> buscarPorId(@PathVariable UUID id,
                                                            @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.buscarPorId(solicitanteId, id));
    }

    /**
     * Atualiza classificados.
     *
     * @param id Identificador único do registro.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas o dono pode editar este anúncio.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Anúncio não encontrado: .
     * @throws IllegalArgumentException se ocorrer a condição: Doação não pode ter preço.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ClassificadoResponse> atualizar(@PathVariable UUID id,
                                                          @Valid @RequestBody AtualizarClassificadoRequest request,
                                                          @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.atualizar(solicitanteId, id, request));
    }

    /**
     * Altera o status de classificados.
     *
     * @param id Identificador único do registro.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Anúncio não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ClassificadoResponse> alterarStatus(@PathVariable UUID id,
                                                              @Valid @RequestBody AlterarStatusClassificadoRequest request,
                                                              @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.alterarStatus(solicitanteId, id, request));
    }
}