package com.eocape.backend.lead;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Expõe os endpoints HTTP de contatos interessados e delega o processamento à camada de serviço.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/lead")
public class LeadController {

    /**
     * Serviço responsável por contatos interessados.
     */
    private final LeadService service;

    /**
     * Inicializa LeadController com os dados e as dependências necessários à operação.
     *
     * @param service Serviço responsável por contatos interessados.
     */
    public LeadController(LeadService service) {
        this.service = service;
    }

    /**
     * Lista contatos interessados.
     *
     * @return coleção dos registros encontrados pela consulta.
     */
    @GetMapping
    public List<Lead> list() {
        return service.listAll();
    }

    /**
     * Cria contatos interessados.
     *
     * @param lead Dados do interessado e do condomínio informados no formulário de contato.
     * @return entidade de lead localizada ou persistida pela operação.
     * @throws IllegalArgumentException se ocorrer a condição: Email inválido..
     */
    @PostMapping
    public Lead create(@Valid @RequestBody Lead lead) {
        return service.create(lead);
    }
}