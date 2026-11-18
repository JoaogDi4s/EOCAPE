package com.eocape.backend.lead;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Aplica as regras de negócio e coordena a persistência de contatos interessados.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Service
public class LeadService {

    /**
     * Repositório de persistência de contatos interessados.
     */
    private final LeadRepository repository;

    /**
     * Inicializa LeadService com os dados e as dependências necessários à operação.
     *
     * @param repository Repositório de persistência de contatos interessados.
     */
    public LeadService(LeadRepository repository) {
        this.repository = repository;
    }

    /**
     * Lista contatos interessados.
     *
     * @return coleção dos registros encontrados pela consulta.
     */
    public List<Lead> listAll() {
        return repository.findAll();
    }

    /**
     * Consulta os registros pelos critérios: identificador do registro.
     *
     * @param id Identificador único do registro.
     * @return entidade de lead localizada ou persistida pela operação.
     * @throws IllegalArgumentException se ocorrer a condição: Lead não encontrado: .
     */
    public Lead findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lead não encontrado: " + id));
    }

    /**
     * Cria contatos interessados.
     *
     * @param lead Dados do interessado e do condomínio informados no formulário de contato.
     * @return entidade de lead localizada ou persistida pela operação.
     * @throws IllegalArgumentException se ocorrer a condição: Email inválido..
     */
    @Transactional
    public Lead create(Lead lead) {
        if (lead.getEmail() == null || !lead.getEmail().matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
            throw new IllegalArgumentException("Email inválido.");
        }
        return repository.save(lead);
    }
}
