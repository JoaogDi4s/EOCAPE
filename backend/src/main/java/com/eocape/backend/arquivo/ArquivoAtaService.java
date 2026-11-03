package com.eocape.backend.arquivo;

import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.arquivo.dto.ArquivoAtaResponse;
import com.eocape.backend.ata.Ata;
import com.eocape.backend.ata.AtaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArquivoAtaService {

    private final ArquivoAtaRepository repository;
    private final AtaRepository ataRepository;
    private final ArquivoRepository arquivoRepository;
    private final AcessoCondominioService acesso;

    /** Só gestor vincula arquivo a uma ata; o arquivo precisa ser do mesmo condomínio. */
    @Transactional
    public ArquivoAtaResponse vincular(UUID solicitanteId, UUID ataId, UUID arquivoId) {
        Ata ata = ataRepository.findById(ataId)
                .orElseThrow(() -> new EntityNotFoundException("Ata não encontrada: " + ataId));
        UUID condominioId = ata.getCondominio().getId();

        acesso.exigirGestor(solicitanteId, condominioId);

        Arquivo arquivo = arquivoRepository.findById(arquivoId)
                .orElseThrow(() -> new EntityNotFoundException("Arquivo não encontrado: " + arquivoId));

        if (!arquivo.getCondominio().getId().equals(condominioId)) {
            throw new IllegalArgumentException("O arquivo não pertence ao condomínio da ata");
        }

        if (repository.existsByAtaIdAndArquivoId(ataId, arquivoId)) {
            throw new IllegalStateException("Este arquivo já está vinculado à ata");
        }

        return ArquivoAtaResponse.from(repository.saveAndFlush(new ArquivoAta(ata, arquivo)));
    }

    /**
     * Lista os anexos da ata. Atas em rascunho só aparecem para gestor,
     * e o morador só vê anexos de atas publicadas.
     */
    @Transactional(readOnly = true)
    public List<ArquivoAtaResponse> listar(UUID solicitanteId, UUID ataId) {
        Ata ata = ataRepository.findById(ataId)
                .orElseThrow(() -> new EntityNotFoundException("Ata não encontrada: " + ataId));
        UUID condominioId = ata.getCondominio().getId();

        acesso.exigirVinculo(solicitanteId, condominioId);

        boolean gestor = acesso.isGestor(solicitanteId, condominioId);
        if (!ata.isPublicada() && !gestor) {
            throw new EntityNotFoundException("Ata não encontrada: " + ataId);
        }

        return repository.findByAtaId(ataId).stream()
                .map(ArquivoAtaResponse::from)
                .toList();
    }

    /** Gestor remove o vínculo (o arquivo continua existindo). */
    @Transactional
    public void desvincular(UUID solicitanteId, UUID vinculoId) {
        ArquivoAta vinculo = repository.findById(vinculoId)
                .orElseThrow(() -> new EntityNotFoundException("Vínculo não encontrado: " + vinculoId));

        acesso.exigirGestor(solicitanteId, vinculo.getAta().getCondominio().getId());

        repository.delete(vinculo);
    }
}