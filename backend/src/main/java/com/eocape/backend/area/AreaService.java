package com.eocape.backend.area;

import com.eocape.backend.area.dto.AreaRequest;
import com.eocape.backend.area.dto.AreaResponse;
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
public class AreaService {

    private final AreaRepository areaRepository;
    private final CondominioRepository condominioRepository;

    @Transactional
    public AreaResponse criar(AreaRequest request) {
        Condominio condominio = condominioRepository.findById(request.condominioId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Condomínio não encontrado: " + request.condominioId()));

        Area area = areaRepository.save(new Area(
                request.nome(),
                condominio,
                request.descricao(),
                request.ativo()));

        return AreaResponse.from(area);
    }

    @Transactional(readOnly = true)
    public List<AreaResponse> listarTodas() {
        return areaRepository.findAll().stream()
                .map(AreaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AreaResponse buscarPorId(UUID id) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Área não encontrada: " + id));
        return AreaResponse.from(area);
    }
}