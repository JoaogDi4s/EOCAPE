package com.eocape.backend.bloco;

import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.bloco.dto.BlocoRequest;
import com.eocape.backend.bloco.dto.BlocoResponse;
import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.condominio.CondominioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Aplica as regras de negócio e coordena a persistência de blocos.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Service
@RequiredArgsConstructor
public class BlocoService {

    /**
     * Repositório de persistência de blocos.
     */
    private final BlocoRepository blocoRepository;
    /**
     * Repositório de persistência de condomínios.
     */
    private final CondominioRepository condominioRepository;
    /**
     * Serviço de verificação dos vínculos e das permissões no condomínio.
     */
    private final AcessoCondominioService acesso;

    /**
     * Cria blocos.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return dados de bloco produzidos pela operação e destinados à resposta da API.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Condomínio não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @Transactional
    public BlocoResponse criar(UUID solicitanteId, BlocoRequest request) {
        Condominio condominio = condominioRepository.findById(request.condominioId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Condomínio não encontrado: " + request.condominioId()));

        acesso.exigirGestor(solicitanteId, condominio.getId());

        Bloco bloco = blocoRepository.save(new Bloco(request.nome(), condominio));
        return BlocoResponse.from(bloco);
    }

    /**
     * Blocos de um condomínio: morador ou gestor.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return coleção dos registros encontrados pela consulta.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @Transactional(readOnly = true)
    public List<BlocoResponse> listar(UUID solicitanteId, UUID condominioId) {
        acesso.exigirVinculo(solicitanteId, condominioId);
        return blocoRepository.findByCondominioId(condominioId).stream()
                .map(BlocoResponse::from)
                .toList();
    }

    /**
     * Busca pelo identificador blocos.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param id Identificador único do registro.
     * @return dados de bloco produzidos pela operação e destinados à resposta da API.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Bloco não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @Transactional(readOnly = true)
    public BlocoResponse buscarPorId(UUID solicitanteId, UUID id) {
        Bloco bloco = blocoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bloco não encontrado: " + id));

        acesso.exigirVinculo(solicitanteId, bloco.getCondominio().getId());
        return BlocoResponse.from(bloco);
    }
}