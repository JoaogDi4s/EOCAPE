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

/**
 * Aplica as regras de negócio e coordena a persistência de arquivos.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Service
@RequiredArgsConstructor
public class ArquivoService {

    /**
     * Prazo, em segundos, durante o qual o link de download permanece válido.
     */
    private static final int URL_VALIDADE_SEGUNDOS = 300;

    /**
     * Repositório de persistência de arquivos.
     */
    private final ArquivoRepository repository;
    /**
     * Repositório de persistência de condomínios.
     */
    private final CondominioRepository condominioRepository;
    /**
     * Repositório de persistência de usuários.
     */
    private final UsuarioRepository usuarioRepository;
    /**
     * Serviço de verificação dos vínculos e das permissões no condomínio.
     */
    private final AcessoCondominioService acesso;
    /**
     * Serviço responsável por supabase storage.
     */
    private final SupabaseStorageService storage;
    /**
     * Repositório de persistência de anexos de atas.
     */
    private final ArquivoAtaRepository arquivosAta;

    /**
     * Registro só de metadados, para arquivos já enviados por outro canal.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return dados de arquivo produzidos pela operação e destinados à resposta da API.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Condomínio não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
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

    /**
     * Valida o vínculo, o tipo e o conteúdo do arquivo, envia os bytes ao storage privado e persiste os metadados.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param tipo Tipo do registro, conforme os valores admitidos pelo domínio.
     * @param conteudo Conteúdo textual do comunicado ou bytes do arquivo, conforme o tipo declarado.
     * @return dados de arquivo produzidos pela operação e destinados à resposta da API.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Condomínio não encontrado: .
     * @throws IllegalArgumentException se ocorrer a condição: Tipo do arquivo não identificado.
     * @throws IllegalStateException se ocorrer a condição: Falha ao ler o arquivo enviado.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
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

    /**
     * Gestor vê todos do condomínio; morador vê só os próprios.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return coleção dos registros encontrados pela consulta.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @Transactional(readOnly = true)
    public List<ArquivoResponse> listar(UUID solicitanteId, UUID condominioId) {
        acesso.exigirVinculo(solicitanteId, condominioId);

        List<Arquivo> arquivos = acesso.isGestor(solicitanteId, condominioId)
                ? repository.findByCondominioIdOrderByCriadoEmDesc(condominioId)
                : repository.findByCondominioIdAndEnviadoPorIdOrderByCriadoEmDesc(condominioId, solicitanteId);

        return arquivos.stream().map(ArquivoResponse::from).toList();
    }

    /**
     * Busca pelo identificador arquivos.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param id Identificador único do registro.
     * @return dados de arquivo produzidos pela operação e destinados à resposta da API.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Você só pode visualizar os seus arquivos.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Arquivo não encontrado: .
     */
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

    /**
     * Autoriza o download para o remetente, gestor ou morador com acesso a uma ata publicada e gera uma URL temporária.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param id Identificador único do registro.
     * @return URL assinada que permite baixar o objeto durante seu prazo de validade.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Este arquivo não está disponível para o seu acesso.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Arquivo não encontrado: .
     */
    @Transactional(readOnly = true)
    public String gerarUrlDownload(UUID solicitanteId, UUID id) {
        Arquivo arquivo = buscar(id);
        UUID condominioId = arquivo.getCondominio().getId();

        acesso.exigirVinculo(solicitanteId, condominioId);

        boolean ehDono = arquivo.getEnviadoPor().getId().equals(solicitanteId);
        boolean ataPublicada = arquivosAta.existsByArquivoIdAndAtaPublicadaTrueAndAtaCondominioId(id, condominioId);
        if (!ehDono && !acesso.isGestor(solicitanteId, condominioId) && !ataPublicada) {
            throw new AccessDeniedException("Este arquivo não está disponível para o seu acesso");
        }
        return storage.gerarUrlAssinada(arquivo.getChaveArmazenamento(), URL_VALIDADE_SEGUNDOS);
    }

    /**
     * Chave: {condominioId}/{pasta}/{uuid}-{nome}. O prefixo do condomínio isola os arquivos.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param tipo Tipo do registro, conforme os valores admitidos pelo domínio.
     * @param nomeOriginal Nome original do arquivo apresentado ao usuário.
     * @return valor textual produzido pela operação.
     */
    private String gerarChave(UUID condominioId, TipoArquivo tipo, String nomeOriginal) {
        return condominioId + "/" + tipo.pasta() + "/" + UUID.randomUUID() + "-" + nomeOriginal;
    }

    /**
     * Remove caminhos e caracteres perigosos do nome enviado pelo cliente.
     *
     * @param nome Nome usado para identificar o registro.
     * @return nome sanitizado para uso na chave de armazenamento.
     */
    private String sanitizar(String nome) {
        if (nome == null || nome.isBlank()) {
            return "arquivo";
        }
        String limpo = nome.replaceAll("[\\\\/]", "_").replaceAll("[^A-Za-z0-9._-]", "_");
        return limpo.length() > 200 ? limpo.substring(limpo.length() - 200) : limpo;
    }

    /**
     * Localiza usuario exigido pela operação.
     *
     * @param id Identificador único do registro.
     * @return entidade de usuario localizada ou persistida pela operação.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Usuário não encontrado.
     */
    private Usuario buscarUsuario(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    /**
     * Busca arquivos.
     *
     * @param id Identificador único do registro.
     * @return entidade de arquivo localizada ou persistida pela operação.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Arquivo não encontrado: .
     */
    private Arquivo buscar(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Arquivo não encontrado: " + id));
    }
}
