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

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Transactional
    public UsuarioResponse cadastrar(CadastroUsuarioRequest request) {
        String cpf = request.cpf().replaceAll("\\D", "");
        String email = request.email().trim().toLowerCase();

        if (usuarioRepository.existsByCpf(cpf)) {
            throw conflito("CPF já cadastrado");
        }
        if (usuarioRepository.existsByEmail(email)) {
            throw conflito("E-mail já cadastrado");
        }

        Usuario usuario = new Usuario(cpf, request.nome().trim(), email, request.telefone().trim());
        usuario.definirSenhaInicial(passwordEncoder.encode(senhaPadrao(request.bloco(), request.apartamento())));

        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

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

    @Transactional
    public void trocarSenha(UUID usuarioId, TrocaSenhaRequest request) {
        validarConfirmacao(request.novaSenha(), request.confirmacaoSenha());
        Usuario usuario = buscar(usuarioId);

        if (!passwordEncoder.matches(request.senhaAtual(), usuario.getSenhaHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha atual incorreta");
        }

        usuario.alterarSenha(passwordEncoder.encode(request.novaSenha()));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(UUID id) {
        return UsuarioResponse.from(buscar(id));
    }

    @Transactional(readOnly = true)
    public PagedModel<UsuarioResponse> listar(Pageable pageable) {
        Page<UsuarioResponse> pagina = usuarioRepository.findAll(pageable)
                .map(UsuarioResponse::from);
        return new PagedModel<>(pagina);
    }

    private Usuario buscar(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    private void validarConfirmacao(String senha, String confirmacao) {
        if (!senha.equals(confirmacao)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "As senhas não conferem");
        }
    }

    private static String senhaPadrao(int bloco, int apartamento) {
        return "B" + bloco + "AP" + apartamento;
    }

    private static ResponseStatusException conflito(String mensagem) {
        return new ResponseStatusException(HttpStatus.CONFLICT, mensagem);
    }
}