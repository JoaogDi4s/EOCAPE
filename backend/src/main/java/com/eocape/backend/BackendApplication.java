package com.eocape.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Inicializa a aplicação Spring Boot e a descoberta dos componentes do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@SpringBootApplication
public class BackendApplication {

	/**
	 * Inicia a aplicação com a configuração do Spring Boot.
	 *
	 * @param args Argumentos de linha de comando fornecidos à aplicação.
	 */
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
