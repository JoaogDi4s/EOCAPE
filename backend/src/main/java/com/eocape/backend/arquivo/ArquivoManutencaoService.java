package com.eocape.backend.arquivo;

import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.arquivo.dto.ArquivoManutencaoResponse;
import com.eocape.backend.manutencao.Manutencao;
import com.eocape.backend.manutencao.ManutencaoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArquivoManutencaoService {

    private final ArquivoManutencaoRepository repository;
    private final ManutencaoRepository manutencaoRepository;
    private final ArquivoRepository arquivoRepository;
    private final AcessoCondominioService acesso;

    /** Só gestor anexa arquivo a manutenção; o arquivo precisa ser do mesmo condomínio. */
    @Transactional
    public ArquivoManutencaoResponse vincular(UUID solicitanteId, UUID manutencaoId, UUID arquivoId) {
        Manutencao manutencao = manutencaoRepository.findById(manutencaoId)
                .orElseThrow(() -> new EntityNotFoundException("Manutenção não encontrada: " + manutencaoId));
        UUID condominioId = manutencao.getCondominio().getId();

        acesso.exigirGestor(solicitanteId, condominioId);

        Arquivo arquivo = arquivoRepository.findById(arquivoId)
                .orElseThrow(() -> new EntityNotFoundException("Arquivo não encontrado: " + arquivoId));

        if (!arquivo.getCondominio().getId().equals(condominioId)) {
            throw new IllegalArgumentException("O arquivo não pertence ao condomínio da manutenção");
        }

        if (repository.existsByManutencaoIdAndArquivoId(manutencaoId, arquivoId)) {
            throw new IllegalStateException("Este arquivo já está vinculado à manutenção");
        }

        return ArquivoManutencaoResponse.from(
                repository.saveAndFlush(new ArquivoManutencao(manutencao, arquivo)));
    }

    /** Qualquer morador ou gestor do condomínio pode ver os anexos de manutenção. */
    @Transactional(readOnly = true)
    public List<ArquivoManutencaoResponse> listar(UUID solicitanteId, UUID manutencaoId) {
        Manutencao manutencao = manutencaoRepository.findById(manutencaoId)
                .orElseThrow(() -> new EntityNotFoundException("Manutenção não encontrada: " + manutencaoId));

        acesso.exigirVinculo(solicitanteId, manutencao.getCondominio().getId());

        return repository.findByManutencaoId(manutencaoId).stream()
                .map(ArquivoManutencaoResponse::from)
                .toList();
    }

    /** Só gestor remove o vínculo; o arquivo continua existindo. */
    @Transactional
    public void desvincular(UUID solicitanteId, UUID vinculoId) {
        ArquivoManutencao vinculo = repository.findById(vinculoId)
                .orElseThrow(() -> new EntityNotFoundException("Vínculo não encontrado: " + vinculoId));

        acesso.exigirGestor(solicitanteId, vinculo.getManutencao().getCondominio().getId());

        repository.delete(vinculo);
    }
}