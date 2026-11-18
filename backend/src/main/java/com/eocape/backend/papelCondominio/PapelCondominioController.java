package com.eocape.backend.papelCondominio;

import com.eocape.backend.papelCondominio.dto.PapelCondominioRequest;
import com.eocape.backend.papelCondominio.dto.PapelCondominioResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Expõe os endpoints HTTP de papéis de acesso ao condomínio e delega o processamento à camada de serviço.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/papel-condominio")
@RequiredArgsConstructor
public class PapelCondominioController {

    /**
     * Serviço responsável por papéis de acesso ao condomínio.
     */
    private final PapelCondominioService service;

    /**
     * Adiciona papéis de acesso ao condomínio.
     *
     * @param request Dados de entrada recebidos e validados para a operação.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return dados de papel condominio produzidos pela operação e destinados à resposta da API.
     * @throws IllegalStateException se ocorrer a condição: Usuário já possui este papel neste condomínio.
     * @throws IllegalArgumentException se ocorrer a condição: Condomínio não encontrado.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PapelCondominioResponse adicionar(@Valid @RequestBody PapelCondominioRequest request,
                                             @AuthenticationPrincipal UUID solicitanteId) {
        return service.adicionarPapel(solicitanteId, request);
    }

    /**
     * Lista os registros vinculados ao usuário autenticado de papéis de acesso ao condomínio.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     */
    @GetMapping("/meus")
    public ResponseEntity<List<PapelCondominioResponse>> listarMeus(@AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listarMeusPapeis(solicitanteId));
    }

    /**
     * Lista os registros do condomínio de papéis de acesso ao condomínio.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @GetMapping("/condominio/{condominioId}")
    public ResponseEntity<List<PapelCondominioResponse>> listarDoCondominio(
            @PathVariable UUID condominioId,
            @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listarDoCondominio(solicitanteId, condominioId));
    }

    /**
     * Desativa papéis de acesso ao condomínio.
     *
     * @param id Identificador único do registro.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @throws IllegalArgumentException se ocorrer a condição: Papel não encontrado.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable UUID id,
                          @AuthenticationPrincipal UUID solicitanteId) {
        service.desativarPapel(solicitanteId, id);
    }
}