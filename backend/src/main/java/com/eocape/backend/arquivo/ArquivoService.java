package com.eocape.backend.arquivo;

import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.arquivo.dto.ArquivoResponse;
import com.eocape.backend.arquivo.dto.RegistrarArquivoRequest;
import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.condominio.CondominioRepository;
import com.eocape.backend.storage.SupabaseStorageService;
import com.eocape.backend.usuario.Usuario;
import com.eocape.backend.usuario.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArquivoService {

    private static final int URL_VALIDADE_SEGUNDOS = 300;

    private final ArquivoRepository repository;
    private final CondominioRepository condominioRepository;
    private final UsuarioRepository usuarioRepository;
    private final AcessoCondominioService acesso;
    private final SupabaseStorageService storage;

    /** Registro só de metadados, para arquivos já enviados por outro canal. */
    @Transactional
    public ArquivoResponse registrar(UUID solicitanteId, RegistrarArquivoRequest request) {
        Condominio condominio = condominioRepository.findById(request.condominioId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Condomínio não encontrado: " + request.condominioId()));

        acesso.exigirVinculo(solicitanteId, condominio.getId());

        Usuario enviadoPor = buscarUsuario(solicitanteId);

        Arquivo arquivo = new Arquivo(condominio, enviadoPor, request.nomeOriginal(),
                request.chaveArmazenamento(), request.tipoMime());

        return ArquivoResponse.from(repository.saveAndFlush(arquivo));
    }

    /** Envia o arquivo ao storage e registra a entidade. A chave é gerada no servidor. */
    @Transactional
    public ArquivoResponse upload(UUID solicitanteId, UUID condominioId,
                                  TipoArquivo tipo, MultipartFile conteudo) {
        Condominio condominio = condominioRepository.findById(condominioId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Condomínio não encontrado: " + condominioId));

        acesso.exigirVinculo(solicitanteId, condominio.getId());

        if (tipo == TipoArquivo.ATA) {
            acesso.exigirGestor(solicitanteId, condominio.getId());
        }

        if (conteudo.isEmpty()) {
            throw new IllegalArgumentException("O arquivo enviado está vazio");
        }

        String tipoMime = conteudo.getContentType();
        if (tipoMime == null || tipoMime.isBlank()) {
            throw new IllegalArgumentException("Tipo do arquivo não identificado");
        }

        String nomeOriginal = sanitizar(conteudo.getOriginalFilename());
        String chave = gerarChave(condominio.getId(), tipo, nomeOriginal);

        try {
            storage.enviar(chave, conteudo.getBytes(), tipoMime);
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao ler o arquivo enviado", e);
        }

        Usuario enviadoPor = buscarUsuario(solicitanteId);
        Arquivo arquivo = new Arquivo(condominio, enviadoPor, nomeOriginal, chave, tipoMime);

        return ArquivoResponse.from(repository.saveAndFlush(arquivo));
    }

    /** Gestor vê todos do condomínio; morador vê só os próprios. */
    @Transactional(readOnly = true)
    public List<ArquivoResponse> listar(UUID solicitanteId, UUID condominioId) {
        acesso.exigirVinculo(solicitanteId, condominioId);

        List<Arquivo> arquivos = acesso.isGestor(solicitanteId, condominioId)
                ? repository.findByCondominioIdOrderByCriadoEmDesc(condominioId)
                : repository.findByCondominioIdAndEnviadoPorIdOrderByCriadoEmDesc(condominioId, solicitanteId);

        return arquivos.stream().map(ArquivoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ArquivoResponse buscarPorId(UUID solicitanteId, UUID id) {
        Arquivo arquivo = buscar(id);
        UUID condominioId = arquivo.getCondominio().getId();

        acesso.exigirVinculo(solicitanteId, condominioId);

        boolean ehDono = arquivo.getEnviadoPor().getId().equals(solicitanteId);
        if (!ehDono && !acesso.isGestor(solicitanteId, condominioId)) {
            throw new AccessDeniedException("Você só pode visualizar os seus arquivos");
        }
        return ArquivoResponse.from(arquivo);
    }

    /** URL de download temporária, só depois da checagem de acesso. */
    @Transactional(readOnly = true)
    public String gerarUrlDownload(UUID solicitanteId, UUID id) {
        Arquivo arquivo = buscar(id);
        UUID condominioId = arquivo.getCondominio().getId();

        acesso.exigirVinculo(solicitanteId, condominioId);

        boolean ehDono = arquivo.getEnviadoPor().getId().equals(solicitanteId);
        if (!ehDono && !acesso.isGestor(solicitanteId, condominioId)) {
            throw new AccessDeniedException("Você só pode baixar os seus arquivos");
        }
        return storage.gerarUrlAssinada(arquivo.getChaveArmazenamento(), URL_VALIDADE_SEGUNDOS);
    }

    /** Chave: {condominioId}/{pasta}/{uuid}-{nome}. O prefixo do condomínio isola os arquivos. */
    private String gerarChave(UUID condominioId, TipoArquivo tipo, String nomeOriginal) {
        return condominioId + "/" + tipo.pasta() + "/" + UUID.randomUUID() + "-" + nomeOriginal;
    }

    /** Remove caminhos e caracteres perigosos do nome enviado pelo cliente. */
    private String sanitizar(String nome) {
        if (nome == null || nome.isBlank()) {
            return "arquivo";
        }
        String limpo = nome.replaceAll("[\\\\/]", "_").replaceAll("[^A-Za-z0-9._-]", "_");
        return limpo.length() > 200 ? limpo.substring(limpo.length() - 200) : limpo;
    }

    private Usuario buscarUsuario(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    private Arquivo buscar(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Arquivo não encontrado: " + id));
    }
}