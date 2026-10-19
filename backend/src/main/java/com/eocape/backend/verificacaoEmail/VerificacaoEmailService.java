package com.eocape.backend.verificacaoEmail;

import com.eocape.backend.common.exception.RegraNegocioException;
import com.eocape.backend.usuario.Usuario;
import com.eocape.backend.usuario.UsuarioRepository;
import com.eocape.backend.verificacaoEmail.dto.request.EnviarCodigoRequest;
import com.eocape.backend.verificacaoEmail.dto.request.VerificarCodigoRequest;
import com.eocape.backend.verificacaoEmail.dto.response.CodigoEnviadoResponse;
import com.eocape.backend.verificacaoEmail.dto.response.CodigoVerificadoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificacaoEmailService {

    private static final int MAX_TENTATIVAS = 5;
    private static final Duration VALIDADE = Duration.ofMinutes(10);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final VerificacaoEmailRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CodigoEmailSender emailSender;
    private final TokenDefinicaoSenhaGenerator tokenGenerator;

    @Transactional
    public CodigoEnviadoResponse enviarCodigo(UUID usuarioId, EnviarCodigoRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado"));

        repository.deleteByUsuarioIdAndUsadoEmIsNull(usuarioId);

        String codigo = String.format("%06d", RANDOM.nextInt(1_000_000));
        VerificacaoEmail verificacao = new VerificacaoEmail(
                usuario, request.email(), passwordEncoder.encode(codigo), VALIDADE);
        repository.save(verificacao);

        emailSender.enviar(request.email(), codigo);
        return new CodigoEnviadoResponse(verificacao.getExpiraEm());
    }

    @Transactional(noRollbackFor = RegraNegocioException.class)
    public CodigoVerificadoResponse verificarCodigo(VerificarCodigoRequest request) {
        VerificacaoEmail verificacao = repository
                .findFirstByEmailAndUsadoEmIsNullOrderByCriadoEmDesc(request.email())
                .orElseThrow(() -> new RegraNegocioException("Nenhum código ativo para este e-mail"));

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
        verificacao.marcarComoUsado();
        String token = tokenGenerator.gerar(verificacao.getUsuario().getId());
        return new CodigoVerificadoResponse(token);
    }
}