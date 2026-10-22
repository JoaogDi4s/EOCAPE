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

@Service
@RequiredArgsConstructor
public class EspacoComumService {

    private final EspacoComumRepository espacoComumRepository;
    private final AreaRepository areaRepository;
    private final AcessoCondominioService acesso;

    @Transactional
    public EspacoComumResponse.Detalhe criar(UUID solicitanteId, EspacoComumRequest.Criar request) {
        Area area = areaRepository.findById(request.areaId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Área não encontrada: " + request.areaId()));

        acesso.exigirGestor(solicitanteId, area.getCondominio().getId());

        EspacoComum espacoComum = espacoComumRepository.save(request.toEntity(area));
        return EspacoComumResponse.Detalhe.from(espacoComum);
    }

    /** Espaços comuns de um condomínio: morador ou gestor. */
    @Transactional(readOnly = true)
    public List<EspacoComumResponse.Detalhe> listar(UUID solicitanteId, UUID condominioId) {
        acesso.exigirVinculo(solicitanteId, condominioId);
        return espacoComumRepository.findByAreaCondominioId(condominioId).stream()
                .map(EspacoComumResponse.Detalhe::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public EspacoComumResponse.Detalhe buscarPorId(UUID solicitanteId, UUID id) {
        EspacoComum espacoComum = espacoComumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Espaço comum não encontrado: " + id));

        acesso.exigirVinculo(solicitanteId, espacoComum.getArea().getCondominio().getId());
        return EspacoComumResponse.Detalhe.from(espacoComum);
    }
}