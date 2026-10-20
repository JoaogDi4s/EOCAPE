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

@Service
@RequiredArgsConstructor
public class BlocoService {

    private final BlocoRepository blocoRepository;
    private final CondominioRepository condominioRepository;
    private final AcessoCondominioService acesso;

    @Transactional
    public BlocoResponse criar(UUID solicitanteId, BlocoRequest request) {
        Condominio condominio = condominioRepository.findById(request.condominioId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Condomínio não encontrado: " + request.condominioId()));

        acesso.exigirGestor(solicitanteId, condominio.getId());

        Bloco bloco = blocoRepository.save(new Bloco(request.nome(), condominio));
        return BlocoResponse.from(bloco);
    }

    /** Blocos de um condomínio: morador ou gestor. */
    @Transactional(readOnly = true)
    public List<BlocoResponse> listar(UUID solicitanteId, UUID condominioId) {
        acesso.exigirVinculo(solicitanteId, condominioId);
        return blocoRepository.findByCondominioId(condominioId).stream()
                .map(BlocoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public BlocoResponse buscarPorId(UUID solicitanteId, UUID id) {
        Bloco bloco = blocoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bloco não encontrado: " + id));

        acesso.exigirVinculo(solicitanteId, bloco.getCondominio().getId());
        return BlocoResponse.from(bloco);
    }
}