package com.eocape.backend.espaco_comum;

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

    @Transactional
    public EspacoComumResponse.Detalhe criar(EspacoComumRequest.Criar request) {
        Area area = areaRepository.findById(request.areaId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Área não encontrada: " + request.areaId()));

        EspacoComum espacoComum = espacoComumRepository.save(request.toEntity(area));
        return EspacoComumResponse.Detalhe.from(espacoComum);
    }

    @Transactional(readOnly = true)
    public List<EspacoComumResponse.Detalhe> listar() {
        return espacoComumRepository.findAll().stream()
                .map(EspacoComumResponse.Detalhe::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public EspacoComumResponse.Detalhe buscarPorId(UUID id) {
        EspacoComum espacoComum = espacoComumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Espaço comum não encontrado: " + id));
        return EspacoComumResponse.Detalhe.from(espacoComum);
    }
}