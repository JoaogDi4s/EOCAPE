package com.eocape.backend.aviso;

import com.eocape.backend.aviso.dto.request.AvisoAtualizacaoRequest;
import com.eocape.backend.aviso.dto.request.AvisoRequest;
import com.eocape.backend.aviso.dto.response.AvisoResponse;
import com.eocape.backend.acesso.AcessoCondominioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Expõe os endpoints HTTP de comunicados e delega o processamento à camada de serviço.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/condominio/{condominioId}/avisos")
@RequiredArgsConstructor
public class AvisoController {

    /**
     * Serviço responsável por comunicados.
     */
    private final AvisoService avisoService;
    /**
     * Serviço de verificação dos vínculos e das permissões no condomínio.
     */
    private final AcessoCondominioService acesso;

    /**
     * Cria comunicados.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @param authentication Contexto de autenticação disponibilizado pelo Spring Security.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @PostMapping
    public ResponseEntity<AvisoResponse> criar(
            @PathVariable UUID condominioId,
            @Valid @RequestBody AvisoRequest request,
            Authentication authentication) {

        UUID autorId = UUID.fromString(authentication.getName());
        acesso.exigirGestor(autorId, condominioId);
        AvisoResponse response = avisoService.criar(condominioId, autorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista comunicados.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param pageable Configuração de paginação e ordenação da consulta.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return página de resultados com os dados e as informações de paginação.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @GetMapping
    public Page<AvisoResponse> listar(
            @PathVariable UUID condominioId,
            @PageableDefault(size = 20, sort = { "importante",
                    "criadoEm" }, direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UUID solicitanteId) {

        acesso.exigirVinculo(solicitanteId, condominioId);
        return avisoService.listar(condominioId, pageable);
    }

    /**
     * Busca comunicados.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param avisoId Identificador de aviso.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return dados de aviso produzidos pela operação e destinados à resposta da API.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @GetMapping("/{avisoId}")
    public AvisoResponse buscar(@PathVariable UUID condominioId, @PathVariable UUID avisoId,
                               @AuthenticationPrincipal UUID solicitanteId) {
        acesso.exigirVinculo(solicitanteId, condominioId);
        return avisoService.buscar(condominioId, avisoId);
    }

    /**
     * Atualiza comunicados.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param avisoId Identificador de aviso.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return dados de aviso produzidos pela operação e destinados à resposta da API.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @PutMapping("/{avisoId}")
    public AvisoResponse atualizar(
            @PathVariable UUID condominioId,
            @PathVariable UUID avisoId,
            @Valid @RequestBody AvisoAtualizacaoRequest request,
            @AuthenticationPrincipal UUID solicitanteId) {

        acesso.exigirGestor(solicitanteId, condominioId);
        return avisoService.atualizar(condominioId, avisoId, request);
    }

    /**
     * Remove comunicados.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param avisoId Identificador de aviso.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @DeleteMapping("/{avisoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable UUID condominioId, @PathVariable UUID avisoId,
                        @AuthenticationPrincipal UUID solicitanteId) {
        acesso.exigirGestor(solicitanteId, condominioId);
        avisoService.remover(condominioId, avisoId);
    }
}
