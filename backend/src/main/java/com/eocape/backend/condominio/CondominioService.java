package com.eocape.backend.condominio;

import com.eocape.backend.condominio.dto.CondominioRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CondominioService {

    private final CondominioRepository repository;

    @Transactional
    public Condominio criar(CondominioRequest request) {
        return repository.save(Condominio.from(request));
    }

    @Transactional(readOnly = true)
    public List<Condominio> listar() {
        return repository.findAll();
    }
}