package com.eocape.backend.ata;

import com.eocape.backend.ata.dto.AtaResponse;
import com.eocape.backend.ata.dto.CriarAtaRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Expõe os endpoints HTTP de atas de assembleia e delega o processamento à camada de serviço.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/condominios/{condominioId}/atas")
@RequiredArgsConstructor
public class AtaController {

    /**
     * Serviço responsável por atas de assembleia.
     */
    private final AtaService ataService;

    /**
     * Lista atas de assembleia.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @return coleção dos registros encontrados pela consulta.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @GetMapping
    public List<AtaResponse> listar(@PathVariable UUID condominioId,
                                    @AuthenticationPrincipal UUID usuarioId) {
        return ataService.listar(condominioId, usuarioId);
    }

    /**
     * Busca atas de assembleia.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param ataId Identificador da ata de assembleia.
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @return dados de ata produzidos pela operação e destinados à resposta da API.
     * @throws org.springframework.web.server.ResponseStatusException se ocorrer a condição: Ata não encontrada.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @GetMapping("/{ataId}")
    public AtaResponse buscar(@PathVariable UUID condominioId,
                              @PathVariable UUID ataId,
                              @AuthenticationPrincipal UUID usuarioId) {
        return ataService.buscar(condominioId, ataId, usuarioId);
    }

    /**
     * Cria atas de assembleia.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @return dados de ata produzidos pela operação e destinados à resposta da API.
     * @throws org.springframework.web.server.ResponseStatusException se ocorrer a condição: Usuário não encontrado.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AtaResponse criar(@PathVariable UUID condominioId,
                             @Valid @RequestBody CriarAtaRequest request,
                             @AuthenticationPrincipal UUID usuarioId) {
        return ataService.criar(condominioId, usuarioId, request);
    }

    /**
     * Publica a ata ou o comunicado de acordo com o recurso desta classe.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param ataId Identificador da ata de assembleia.
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @return dados de ata produzidos pela operação e destinados à resposta da API.
     * @throws org.springframework.web.server.ResponseStatusException se ocorrer a condição: Ata não encontrada.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @PatchMapping("/{ataId}/publicar")
    public AtaResponse publicar(@PathVariable UUID condominioId,
                                @PathVariable UUID ataId,
                                @AuthenticationPrincipal UUID usuarioId) {
        return ataService.publicar(condominioId, ataId, usuarioId);
    }
}