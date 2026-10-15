package com.eocape.backend.unidade;

import com.eocape.backend.bloco.Bloco;
import com.eocape.backend.bloco.BlocoRepository;
import com.eocape.backend.unidade.dto.UnidadeRequest;
import com.eocape.backend.unidade.dto.UnidadeResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnidadeService {

    private final UnidadeRepository unidadeRepository;
    private final BlocoRepository blocoRepository;

    @Transactional
    public UnidadeResponse criar(UnidadeRequest request) {
        Bloco bloco = blocoRepository.findById(request.blocoId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Bloco não encontrado: " + request.blocoId()));

        Unidade unidade = unidadeRepository.save(
                new Unidade(bloco, request.numero(), request.andar()));
        return UnidadeResponse.from(unidade);
    }

    @Transactional(readOnly = true)
    public List<UnidadeResponse> listar() {
        return unidadeRepository.findAll().stream()
                .map(UnidadeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public UnidadeResponse buscarPorId(UUID id) {
        Unidade unidade = unidadeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Unidade não encontrada: " + id));
        return UnidadeResponse.from(unidade);
    }

}