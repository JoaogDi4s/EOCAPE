package com.eocape.backend.condominio;

import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.papelCondominio.PapelCondominioRepository;
import com.eocape.backend.vinculoUnidade.VinculoUnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Aplica as regras de negócio e coordena a persistência de condomínios.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Service
@RequiredArgsConstructor
public class CondominioService {

    /**
     * Repositório de persistência de condomínios.
     */
    private final CondominioRepository repository;
    /**
     * Repositório de persistência de papéis de acesso ao condomínio.
     */
    private final PapelCondominioRepository papelRepository;
    /**
     * Repositório de persistência de vínculos entre usuários e unidades.
     */
    private final VinculoUnidadeRepository vinculoRepository;
    /**
     * Serviço de verificação dos vínculos e das permissões no condomínio.
     */
    private final AcessoCondominioService acesso;

    /**
     * Condomínios em que o usuário tem papel ativo ou vínculo ativo.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return coleção dos registros encontrados pela consulta.
     */
    @Transactional(readOnly = true)
    public List<Condominio> listarMeus(UUID solicitanteId) {
        Set<UUID> ids = papelRepository.findByUsuarioIdAndAtivoTrue(solicitanteId).stream()
                .map(p -> p.getCondominio().getId())
                .collect(Collectors.toSet());

        vinculoRepository.findByUsuarioId(solicitanteId).stream()
                .filter(v -> v.isAtivo())
                .forEach(v -> ids.add(v.getUnidade().getBloco().getCondominio().getId()));

        return repository.findAllById(ids);
    }

    /**
     * Busca pelo identificador condomínios.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param id Identificador único do registro.
     * @return entidade de condominio localizada ou persistida pela operação.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Condomínio não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @Transactional(readOnly = true)
    public Condominio buscarPorId(UUID solicitanteId, UUID id) {
        acesso.exigirVinculo(solicitanteId, id);
        return repository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Condomínio não encontrado: " + id));
    }
}