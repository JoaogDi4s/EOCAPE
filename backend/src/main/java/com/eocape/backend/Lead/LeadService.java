package com.eocape.backend.Lead;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class LeadService {

    private final LeadRepository repository;

    public LeadService(LeadRepository repository) {
        this.repository = repository;
    }

    public List<Lead> listAll() {
        return repository.findAll();
    }

    public Lead findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lead não encontrado: " + id));
    }

    @Transactional
    public Lead create(Lead lead) {
        if (lead.getEmail() == null || !lead.getEmail().matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
            throw new IllegalArgumentException("Email inválido.");
        }
        return repository.save(lead);
    }
}
