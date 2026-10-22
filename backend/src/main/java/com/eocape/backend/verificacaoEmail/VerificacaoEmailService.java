package com.eocape.backend.verificacaoEmail;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eocape.backend.auth.Finalidade;
import com.eocape.backend.auth.TokenService;
import com.eocape.backend.common.exception.RegraNegocioException;
import com.eocape.backend.usuario.Usuario;
import com.eocape.backend.usuario.UsuarioRepository;
import com.eocape.backend.verificacaoEmail.dto.request.EnviarCodigoRequest;
import com.eocape.backend.verificacaoEmail.dto.request.VerificarCodigoRequest;
import com.eocape.backend.verificacaoEmail.dto.response.CodigoEnviadoResponse;
import com.eocape.backend.verificacaoEmail.dto.response.CodigoVerificadoResponse;

@Service
public class VerificacaoEmailService {

    private static final int MAX_TENTATIVAS = 5;
    private static final Duration VALIDADE = Duration.ofMinutes(10);
    private static final Duration INTERVALO_REENVIO = Duration.ofSeconds(60);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final VerificacaoEmailRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CodigoEmailSender emailSender;
    private final TokenService tokenService;

    public VerificacaoEmailService(VerificacaoEmailRepository repository,
                                   UsuarioRepository usuarioRepository,
                                   PasswordEncoder passwordEncoder,
                                   CodigoEmailSender emailSender,
                                   TokenService tokenService) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
        this.tokenService = tokenService;
    }

    @Transactional
    public CodigoEnviadoResponse enviarCodigo(UUID usuarioId, EnviarCodigoRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado"));
        String email = request.email().trim().toLowerCase();

        if (!email.equals(usuario.getEmail()) && usuarioRepository.existsByEmail(email)) {
            throw new RegraNegocioException("Este e-mail já está em uso");
        }

        repository.findFirstByUsuarioIdOrderByCriadoEmDesc(usuarioId)
                .filter(ultimo -> ultimo.getCriadoEm() != null
                        && ultimo.getCriadoEm().plus(INTERVALO_REENVIO).isAfter(Instant.now()))
                .ifPresent(ultimo -> {
                    throw new RegraNegocioException("Aguarde um minuto antes de solicitar outro código");
                });

        repository.deleteByUsuarioIdAndUsadoEmIsNull(usuarioId);

        String codigo = String.format("%06d", RANDOM.nextInt(1_000_000));
        VerificacaoEmail verificacao = new VerificacaoEmail(
                usuario, email, passwordEncoder.encode(codigo), VALIDADE);
        repository.save(verificacao);

        emailSender.enviar(email, codigo);
        return new CodigoEnviadoResponse(verificacao.getExpiraEm());
    }

    @Transactional(noRollbackFor = RegraNegocioException.class)
    public CodigoVerificadoResponse verificarCodigo(UUID usuarioId, VerificarCodigoRequest request) {
        String email = request.email().trim().toLowerCase();

        VerificacaoEmail verificacao = repository
                .findFirstByUsuarioIdAndUsadoEmIsNullOrderByCriadoEmDesc(usuarioId)
                .orElseThrow(() -> new RegraNegocioException("Nenhum código ativo"));

        if (!verificacao.getEmail().equals(email)) {
            throw new RegraNegocioException("Nenhum código ativo para este e-mail");
        }
        if (verificacao.estaExpirada()) {
            throw new RegraNegocioException("Código expirado, solicite um novo");
        }
        if (verificacao.getTentativas() >= MAX_TENTATIVAS) {
            throw new RegraNegocioException("Limite de tentativas atingido, solicite um novo código");
        }
        if (!passwordEncoder.matches(request.codigo(), verificacao.getCodigoHash())) {
            verificacao.registrarTentativa();
            throw new RegraNegocioException("Código inválido");
        }

        Usuario usuario = verificacao.getUsuario();
        String novoEmail = verificacao.getEmail();

        // Só checa conflito se o e-mail mudou; o próprio usuário já possui o e-mail atual
        if (!novoEmail.equals(usuario.getEmail()) && usuarioRepository.existsByEmail(novoEmail)) {
            throw new RegraNegocioException("Este e-mail já está em uso");
        }

        verificacao.marcarComoUsado();
        usuario.atualizarEmail(novoEmail);

        return new CodigoVerificadoResponse(
                tokenService.gerar(usuario.getId(), Finalidade.DEFINIR_SENHA));
    }
}