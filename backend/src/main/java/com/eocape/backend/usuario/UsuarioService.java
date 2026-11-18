package com.eocape.backend.usuario;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.eocape.backend.auth.Finalidade;
import com.eocape.backend.auth.TokenService;
import com.eocape.backend.usuario.dto.request.CadastroUsuarioRequest;
import com.eocape.backend.usuario.dto.request.DefinirSenhaRequest;
import com.eocape.backend.usuario.dto.request.LoginRequest;
import com.eocape.backend.usuario.dto.request.TrocaSenhaRequest;
import com.eocape.backend.usuario.dto.response.LoginResponse;
import com.eocape.backend.usuario.dto.response.UsuarioResponse;

/**
 * Aplica as regras de negócio e coordena a persistência de usuários.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Service
public class UsuarioService {

    /**
     * Repositório de persistência de usuários.
     */
    private final UsuarioRepository usuarioRepository;
    /**
     * Codificador usado para gerar e verificar hashes de senhas e códigos.
     */
    private final PasswordEncoder passwordEncoder;
    /**
     * Serviço de emissão e validação dos tokens JWT.
     */
    private final TokenService tokenService;

    /**
     * Inicializa UsuarioService com os dados e as dependências necessários à operação.
     *
     * @param usuarioRepository Repositório de persistência de usuários.
     * @param passwordEncoder Codificador usado para gerar e verificar hashes de senhas e códigos.
     * @param tokenService Serviço de emissão e validação dos tokens JWT.
     */
    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    /**
     * Normaliza CPF e e-mail, rejeita duplicidades e cadastra o usuário com a senha inicial derivada do bloco e do apartamento.
     *
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return dados públicos do usuário cadastrado, sem expor o hash da senha.
     * @throws org.springframework.web.server.ResponseStatusException se a regra de negócio ou o estado atual não permitir a operação.
     */
    @Transactional
    public UsuarioResponse cadastrar(CadastroUsuarioRequest request) {
        String cpf = request.cpf().replaceAll("\\D", "");
        String email = request.email().trim().toLowerCase();

        if (usuarioRepository.existsByCpf(cpf)) {
            throw conflito("CPF já cadastrado. Se deseja adicionar uma unidade a essa pessoa, escolha Vincular cadastro existente.");
        }
        if (usuarioRepository.existsByEmail(email)) {
            throw conflito("E-mail já cadastrado");
        }

        Usuario usuario = new Usuario(cpf, request.nome().trim(), email, request.telefone().trim());
        usuario.definirSenhaInicial(passwordEncoder.encode(senhaPadrao(request.bloco(), request.apartamento())));

        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    /**
     * Autentica o usuário por CPF e senha e emite um token adequado ao estágio do primeiro acesso.
     *
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return token JWT e indicação de que a definição de senha é obrigatória no primeiro acesso.
     * @throws org.springframework.web.server.ResponseStatusException se ocorrer a condição: CPF ou senha inválidos.
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String cpf = request.cpf().replaceAll("\\D", "");
        Usuario usuario = usuarioRepository.findByCpf(cpf).orElse(null);

        // Mesma mensagem para CPF inexistente e senha errada
        if (usuario == null
                || !usuario.isAtivo()
                || usuario.getSenhaHash() == null
                || !passwordEncoder.matches(request.senha(), usuario.getSenhaHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "CPF ou senha inválidos");
        }

        if (!usuario.isPrimeiroAcessoConcluido()) {
            return new LoginResponse(
                    tokenService.gerar(usuario.getId(), Finalidade.PRIMEIRO_ACESSO), true);
        }
        return new LoginResponse(tokenService.gerar(usuario.getId(), Finalidade.ACESSO), false);
    }

    /**
     * Conclui o primeiro acesso com a nova senha e emite o token de acesso.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return token de acesso emitido após a definição da senha.
     * @throws org.springframework.web.server.ResponseStatusException se a regra de negócio ou o estado atual não permitir a operação.
     */
    @Transactional
    public LoginResponse definirSenha(UUID usuarioId, DefinirSenhaRequest request) {
        validarConfirmacao(request.novaSenha(), request.confirmacaoSenha());
        Usuario usuario = buscar(usuarioId);

        if (usuario.isPrimeiroAcessoConcluido()) {
            throw conflito("Primeiro acesso já concluído");
        }

        usuario.concluirPrimeiroAcesso(passwordEncoder.encode(request.novaSenha()));
        return new LoginResponse(tokenService.gerar(usuario.getId(), Finalidade.ACESSO), false);
    }

    /**
     * Confirma a senha atual e substitui o hash pela nova senha informada.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @throws org.springframework.web.server.ResponseStatusException se ocorrer a condição: Senha atual incorreta.
     */
    @Transactional
    public void trocarSenha(UUID usuarioId, TrocaSenhaRequest request) {
        validarConfirmacao(request.novaSenha(), request.confirmacaoSenha());
        Usuario usuario = buscar(usuarioId);

        if (!passwordEncoder.matches(request.senhaAtual(), usuario.getSenhaHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha atual incorreta");
        }

        usuario.alterarSenha(passwordEncoder.encode(request.novaSenha()));
    }

    /**
     * Busca pelo identificador usuários.
     *
     * @param id Identificador único do registro.
     * @return dados de usuario produzidos pela operação e destinados à resposta da API.
     * @throws org.springframework.web.server.ResponseStatusException se ocorrer a condição: Usuário não encontrado.
     */
    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(UUID id) {
        return UsuarioResponse.from(buscar(id));
    }

    /**
     * Lista usuários.
     *
     * @param pageable Configuração de paginação e ordenação da consulta.
     * @return página de resultados com os dados e as informações de paginação.
     */
    @Transactional(readOnly = true)
    public PagedModel<UsuarioResponse> listar(Pageable pageable) {
        Page<UsuarioResponse> pagina = usuarioRepository.findAll(pageable)
                .map(UsuarioResponse::from);
        return new PagedModel<>(pagina);
    }

    /**
     * Busca usuários.
     *
     * @param id Identificador único do registro.
     * @return entidade de usuario localizada ou persistida pela operação.
     * @throws org.springframework.web.server.ResponseStatusException se ocorrer a condição: Usuário não encontrado.
     */
    private Usuario buscar(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    /**
     * Exige que a senha e sua confirmação sejam iguais.
     *
     * @param senha Senha em texto simples recebida para validação; não deve ser registrada em logs.
     * @param confirmacao Valor de confirmação que deve corresponder à senha informada.
     * @throws org.springframework.web.server.ResponseStatusException se ocorrer a condição: As senhas não conferem.
     */
    private void validarConfirmacao(String senha, String confirmacao) {
        if (!senha.equals(confirmacao)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "As senhas não conferem");
        }
    }

    /**
     * Compõe a senha inicial a partir dos números do bloco e do apartamento.
     *
     * @param bloco Bloco relacionado ao registro; em dados de cadastro, número usado na senha inicial.
     * @param apartamento Número do apartamento usado na composição da senha inicial.
     * @return senha inicial formada pelos identificadores do bloco e do apartamento.
     */
    private static String senhaPadrao(int bloco, int apartamento) {
        return "B" + bloco + "AP" + apartamento;
    }

    /**
     * Cria uma exceção para indicar conflito com o estado atual do recurso.
     *
     * @param mensagem Mensagem descritiva da regra de negócio ou do resultado.
     * @return exceção configurada com o status e a mensagem correspondentes.
     */
    private static ResponseStatusException conflito(String mensagem) {
        return new ResponseStatusException(HttpStatus.CONFLICT, mensagem);
    }
}
