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

@Service
@RequiredArgsConstructor
public class UnidadeService {

    private final UnidadeRepository unidadeRepository;
    private final BlocoRepository blocoRepository;
    private final AcessoCondominioService acesso;

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

    /** Unidades de um condomínio: morador ou gestor. */
    @Transactional(readOnly = true)
    public List<UnidadeResponse> listar(UUID solicitanteId, UUID condominioId) {
        acesso.exigirVinculo(solicitanteId, condominioId);
        return unidadeRepository.findByBlocoCondominioId(condominioId).stream()
                .map(UnidadeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public UnidadeResponse buscarPorId(UUID solicitanteId, UUID id) {
        Unidade unidade = unidadeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Unidade não encontrada: " + id));

        acesso.exigirVinculo(solicitanteId, unidade.getBloco().getCondominio().getId());
        return UnidadeResponse.from(unidade);
    }
}