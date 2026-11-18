package com.eocape.backend.espaco_comum;

import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.area.Area;
import com.eocape.backend.area.AreaRepository;
import com.eocape.backend.espaco_comum.dto.EspacoComumRequest;
import com.eocape.backend.espaco_comum.dto.EspacoComumResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Aplica as regras de negócio e coordena a persistência de espaços comuns.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Service
@RequiredArgsConstructor
public class EspacoComumService {

    /**
     * Repositório de persistência de espaços comuns.
     */
    private final EspacoComumRepository espacoComumRepository;
    /**
     * Repositório de persistência de áreas.
     */
    private final AreaRepository areaRepository;
    /**
     * Serviço de verificação dos vínculos e das permissões no condomínio.
     */
    private final AcessoCondominioService acesso;

    /**
     * Cria espaços comuns.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return resultado da operação no tipo EspacoComumResponse.Detalhe.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Área não encontrada: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @Transactional
    public EspacoComumResponse.Detalhe criar(UUID solicitanteId, EspacoComumRequest.Criar request) {
        Area area = areaRepository.findById(request.areaId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Área não encontrada: " + request.areaId()));

        acesso.exigirGestor(solicitanteId, area.getCondominio().getId());

        EspacoComum espacoComum = espacoComumRepository.save(request.toEntity(area));
        return EspacoComumResponse.Detalhe.from(espacoComum);
    }

    /**
     * Espaços comuns de um condomínio: morador ou gestor.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return coleção dos registros encontrados pela consulta.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @Transactional(readOnly = true)
    public List<EspacoComumResponse.Detalhe> listar(UUID solicitanteId, UUID condominioId) {
        acesso.exigirVinculo(solicitanteId, condominioId);
        return espacoComumRepository.findByAreaCondominioId(condominioId).stream()
                .map(EspacoComumResponse.Detalhe::from)
                .toList();
    }

    /**
     * Busca pelo identificador espaços comuns.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param id Identificador único do registro.
     * @return resultado da operação no tipo EspacoComumResponse.Detalhe.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Espaço comum não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @Transactional(readOnly = true)
    public EspacoComumResponse.Detalhe buscarPorId(UUID solicitanteId, UUID id) {
        EspacoComum espacoComum = espacoComumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Espaço comum não encontrado: " + id));

        acesso.exigirVinculo(solicitanteId, espacoComum.getArea().getCondominio().getId());
        return EspacoComumResponse.Detalhe.from(espacoComum);
    }
}