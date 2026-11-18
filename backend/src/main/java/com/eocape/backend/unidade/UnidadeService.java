package com.eocape.backend.unidade;

import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.bloco.Bloco;
import com.eocape.backend.bloco.BlocoRepository;
import com.eocape.backend.unidade.dto.UnidadeRequest;
import com.eocape.backend.unidade.dto.UnidadeResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Aplica as regras de negócio e coordena a persistência de unidades.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Service
@RequiredArgsConstructor
public class UnidadeService {

    /**
     * Repositório de persistência de unidades.
     */
    private final UnidadeRepository unidadeRepository;
    /**
     * Repositório de persistência de blocos.
     */
    private final BlocoRepository blocoRepository;
    /**
     * Serviço de verificação dos vínculos e das permissões no condomínio.
     */
    private final AcessoCondominioService acesso;

    /**
     * Cria unidades.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return dados de unidade produzidos pela operação e destinados à resposta da API.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Bloco não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @Transactional
    public UnidadeResponse criar(UUID solicitanteId, UnidadeRequest request) {
        Bloco bloco = blocoRepository.findById(request.blocoId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Bloco não encontrado: " + request.blocoId()));

        acesso.exigirGestor(solicitanteId, bloco.getCondominio().getId());

        Unidade unidade = unidadeRepository.save(
                new Unidade(bloco, request.numero(), request.andar()));
        return UnidadeResponse.from(unidade);
    }

    /**
     * Unidades de um condomínio: morador ou gestor.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return coleção dos registros encontrados pela consulta.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @Transactional(readOnly = true)
    public List<UnidadeResponse> listar(UUID solicitanteId, UUID condominioId) {
        acesso.exigirVinculo(solicitanteId, condominioId);
        return unidadeRepository.findByBlocoCondominioId(condominioId).stream()
                .map(UnidadeResponse::from)
                .toList();
    }

    /**
     * Busca pelo identificador unidades.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param id Identificador único do registro.
     * @return dados de unidade produzidos pela operação e destinados à resposta da API.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Unidade não encontrada: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @Transactional(readOnly = true)
    public UnidadeResponse buscarPorId(UUID solicitanteId, UUID id) {
        Unidade unidade = unidadeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Unidade não encontrada: " + id));

        acesso.exigirVinculo(solicitanteId, unidade.getBloco().getCondominio().getId());
        return UnidadeResponse.from(unidade);
    }
}