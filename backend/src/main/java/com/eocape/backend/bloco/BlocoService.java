package com.eocape.backend.bloco;

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

    @Transactional
    public BlocoResponse criar(BlocoRequest request) {
        Condominio condominio = condominioRepository.findById(request.condominioId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Condomínio não encontrado: " + request.condominioId()));

        Bloco bloco = blocoRepository.save(new Bloco(request.nome(), condominio));
        return BlocoResponse.from(bloco);
    }

    @Transactional(readOnly = true)
    public List<BlocoResponse> listar() {
        return blocoRepository.findAll().stream()
                .map(BlocoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public BlocoResponse buscarPorId(UUID id) {
        Bloco bloco = blocoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bloco não encontrado: " + id));
        return BlocoResponse.from(bloco);
    }
}