package com.eocape.backend.ata;

import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.ata.dto.AtaResponse;
import com.eocape.backend.ata.dto.CriarAtaRequest;
import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.condominio.CondominioRepository;
import com.eocape.backend.usuario.Usuario;
import com.eocape.backend.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * Aplica as regras de negócio e coordena a persistência de atas de assembleia.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Service
@RequiredArgsConstructor
public class AtaService {

    /**
     * Repositório de persistência de atas de assembleia.
     */
    private final AtaRepository ataRepository;
    /**
     * Repositório de persistência de condomínios.
     */
    private final CondominioRepository condominioRepository;
    /**
     * Repositório de persistência de usuários.
     */
    private final UsuarioRepository usuarioRepository;
    /**
     * Serviço de verificação dos vínculos e das permissões no condomínio.
     */
    private final AcessoCondominioService acesso;

    /**
     * Lista atas de assembleia.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @return coleção dos registros encontrados pela consulta.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @Transactional(readOnly = true)
    public List<AtaResponse> listar(UUID condominioId, UUID usuarioId) {
        acesso.exigirVinculo(usuarioId, condominioId);

        List<Ata> atas = acesso.isGestor(usuarioId, condominioId)
                ? ataRepository.findByCondominioIdOrderByDataReuniaoDesc(condominioId)
                : ataRepository.findByCondominioIdAndPublicadaTrueOrderByDataReuniaoDesc(condominioId);

        return atas.stream().map(AtaResponse::from).toList();
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
    @Transactional(readOnly = true)
    public AtaResponse buscar(UUID condominioId, UUID ataId, UUID usuarioId) {
        acesso.exigirVinculo(usuarioId, condominioId);

        Ata ata = ataRepository.findByIdAndCondominioId(ataId, condominioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ata não encontrada"));

        // Morador não vê rascunho: responde como se a ata não existisse
        if (!ata.isPublicada() && !acesso.isGestor(usuarioId, condominioId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ata não encontrada");
        }

        return AtaResponse.from(ata);
    }

    /**
     * Cria atas de assembleia.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return dados de ata produzidos pela operação e destinados à resposta da API.
     * @throws org.springframework.web.server.ResponseStatusException se ocorrer a condição: Usuário não encontrado.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @Transactional
    public AtaResponse criar(UUID condominioId, UUID usuarioId, CriarAtaRequest request) {
        acesso.exigirGestor(usuarioId, condominioId);

        Condominio condominio = condominioRepository.findById(condominioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Condomínio não encontrado"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        Ata ata = new Ata(condominio, usuario, request.titulo(), request.dataReuniao(), request.resumo());
        if (request.publicada()) {
            ata.publicar();
        }

        return AtaResponse.from(ataRepository.save(ata));
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
    @Transactional
    public AtaResponse publicar(UUID condominioId, UUID ataId, UUID usuarioId) {
        acesso.exigirGestor(usuarioId, condominioId);

        Ata ata = ataRepository.findByIdAndCondominioId(ataId, condominioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ata não encontrada"));

        ata.publicar();
        return AtaResponse.from(ata);
    }
}