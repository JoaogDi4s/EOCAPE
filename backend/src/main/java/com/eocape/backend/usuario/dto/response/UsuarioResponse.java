package com.eocape.backend.usuario.dto.response;
import com.eocape.backend.usuario.Usuario;
import java.util.UUID;

/**
 * Representa os dados de usuario devolvidos pela aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param id Identificador único do registro.
 * @param nome Nome usado para identificar o registro.
 * @param email Endereço de e-mail do usuário.
 * @param telefone Número de telefone de contato.
 * @param primeiroAcessoConcluido Indica se o usuário concluiu a ativação e definiu a própria senha.
 */
public record UsuarioResponse(
    UUID id,
    String nome,
    String email,
    String telefone,
    boolean primeiroAcessoConcluido
) {

    /**
     * Converte a entidade de domínio para os dados expostos na resposta da API.
     *
     * @param usuario Usuário associado ao registro.
     * @return dados de usuario produzidos pela operação e destinados à resposta da API.
     */
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getTelefone(),
            usuario.isPrimeiroAcessoConcluido()
        );
    }
}