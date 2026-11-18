package com.eocape.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Expõe os endpoints HTTP de hello e delega o processamento à camada de serviço.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@RestController
public class HelloController {

    /**
     * Responde à requisição de verificação de disponibilidade da API.
     *
     * @return valor textual produzido pela operação.
     */
    @GetMapping("/api/ping")
    public String ping() {
        return "EOCAPE backend funcionando!";
    }
}