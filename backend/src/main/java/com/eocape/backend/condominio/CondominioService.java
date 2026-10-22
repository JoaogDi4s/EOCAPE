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

@Service
@RequiredArgsConstructor
public class CondominioService {

    private final CondominioRepository repository;
    private final PapelCondominioRepository papelRepository;
    private final VinculoUnidadeRepository vinculoRepository;
    private final AcessoCondominioService acesso;

    /** Condomínios em que o usuário tem papel ativo ou vínculo ativo. */
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

    @Transactional(readOnly = true)
    public Condominio buscarPorId(UUID solicitanteId, UUID id) {
        acesso.exigirVinculo(solicitanteId, id);
        return repository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Condomínio não encontrado: " + id));
    }
}