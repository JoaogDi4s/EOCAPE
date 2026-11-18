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

/**
 * Aplica as regras de negócio e coordena a persistência de verificações de e-mail.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Service
public class VerificacaoEmailService {

    /**
     * Quantidade máxima de tentativas permitidas para verificar um código.
     */
    private static final int MAX_TENTATIVAS = 5;
    /**
     * Intervalo durante o qual o código de verificação permanece válido.
     */
    private static final Duration VALIDADE = Duration.ofMinutes(10);
    /**
     * Intervalo mínimo entre solicitações de novos códigos.
     */
    private static final Duration INTERVALO_REENVIO = Duration.ofSeconds(60);
    /**
     * Gerador criptograficamente seguro dos códigos de verificação.
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Repositório de persistência de verificações de e-mail.
     */
    private final VerificacaoEmailRepository repository;
    /**
     * Repositório de persistência de usuários.
     */
    private final UsuarioRepository usuarioRepository;
    /**
     * Codificador usado para gerar e verificar hashes de senhas e códigos.
     */
    private final PasswordEncoder passwordEncoder;
    /**
     * Componente responsável pelo envio das mensagens de verificação.
     */
    private final CodigoEmailSender emailSender;
    /**
     * Serviço de emissão e validação dos tokens JWT.
     */
    private final TokenService tokenService;

    /**
     * Inicializa VerificacaoEmailService com os dados e as dependências necessários à operação.
     *
     * @param repository Repositório de persistência de verificações de e-mail.
     * @param usuarioRepository Repositório de persistência de usuários.
     * @param passwordEncoder Codificador usado para gerar e verificar hashes de senhas e códigos.
     * @param emailSender Componente responsável pelo envio das mensagens de verificação.
     * @param tokenService Serviço de emissão e validação dos tokens JWT.
     */
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

    /**
     * Cria e envia um código temporário, respeitando o intervalo entre reenvios.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return instante de expiração do código enviado ao endereço informado.
     * @throws com.eocape.backend.common.exception.RegraNegocioException se ocorrer a condição: Aguarde um minuto antes de solicitar outro código.
     */
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

    /**
     * Valida o código de e-mail e emite a autorização para definir a senha.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return token de definição de senha após a confirmação do código.
     * @throws com.eocape.backend.common.exception.RegraNegocioException se ocorrer a condição: Este e-mail já está em uso.
     */
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