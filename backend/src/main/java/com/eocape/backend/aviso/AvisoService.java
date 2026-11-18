package com.eocape.backend.aviso;

import com.eocape.backend.aviso.dto.request.AvisoAtualizacaoRequest;
import com.eocape.backend.aviso.dto.request.AvisoRequest;
import com.eocape.backend.aviso.dto.response.AvisoResponse;
import com.eocape.backend.bloco.Bloco;
import com.eocape.backend.bloco.BlocoRepository;
import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.condominio.CondominioRepository;
import com.eocape.backend.usuario.Usuario;
import com.eocape.backend.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * Aplica as regras de negócio e coordena a persistência de comunicados.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AvisoService {

    /**
     * Repositório de persistência de comunicados.
     */
    private final AvisoRepository avisoRepository;
    /**
     * Repositório de persistência de condomínios.
     */
    private final CondominioRepository condominioRepository;
    /**
     * Repositório de persistência de usuários.
     */
    private final UsuarioRepository usuarioRepository;
    /**
     * Repositório de persistência de blocos.
     */
    private final BlocoRepository blocoRepository;

    /**
     * Cria comunicados.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param autorId Identificador do autor do comunicado.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return dados de aviso produzidos pela operação e destinados à resposta da API.
     */
    @Transactional
    public AvisoResponse criar(UUID condominioId, UUID autorId, AvisoRequest request) {
        Condominio condominio = condominioRepository.findById(condominioId)
                .orElseThrow(() -> naoEncontrado("Condomínio não encontrado"));

        Usuario autor = usuarioRepository.findById(autorId)
                .orElseThrow(() -> naoEncontrado("Usuário não encontrado"));

        Bloco bloco = blocoRepository.findById(request.blocoId())
                .filter(b -> b.getCondominio().getId().equals(condominioId))
                .orElseThrow(() -> naoEncontrado("Bloco não encontrado neste condomínio"));

        Aviso aviso = new Aviso(condominio, autor, bloco,
                request.titulo(), request.conteudo(), request.importante());

        return AvisoResponse.from(avisoRepository.save(aviso));
    }

    /**
     * Lista comunicados.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param pageable Configuração de paginação e ordenação da consulta.
     * @return página de resultados com os dados e as informações de paginação.
     */
    public Page<AvisoResponse> listar(UUID condominioId, Pageable pageable) {
        return avisoRepository.findByCondominioId(condominioId, pageable)
                .map(AvisoResponse::from);
    }

    /**
     * Busca comunicados.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param avisoId Identificador de aviso.
     * @return dados de aviso produzidos pela operação e destinados à resposta da API.
     */
    public AvisoResponse buscar(UUID condominioId, UUID avisoId) {
        return AvisoResponse.from(buscarEntidade(condominioId, avisoId));
    }

    /**
     * Atualiza comunicados.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param avisoId Identificador de aviso.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return dados de aviso produzidos pela operação e destinados à resposta da API.
     */
    @Transactional
    public AvisoResponse atualizar(UUID condominioId, UUID avisoId, AvisoAtualizacaoRequest request) {
        Aviso aviso = buscarEntidade(condominioId, avisoId);
        aviso.atualizar(request.titulo(), request.conteudo(), request.importante());
        return AvisoResponse.from(aviso);
    }

    /**
     * Remove comunicados.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param avisoId Identificador de aviso.
     */
    @Transactional
    public void remover(UUID condominioId, UUID avisoId) {
        avisoRepository.delete(buscarEntidade(condominioId, avisoId));
    }

    /**
     * Localiza entidade exigido pela operação.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param avisoId Identificador de aviso.
     * @return entidade de aviso localizada ou persistida pela operação.
     */
    private Aviso buscarEntidade(UUID condominioId, UUID avisoId) {
        return avisoRepository.findByIdAndCondominioId(avisoId, condominioId)
                .orElseThrow(() -> naoEncontrado("Aviso não encontrado"));
    }

    /**
     * Representa a ausência do recurso solicitado no tratamento HTTP.
     *
     * @param mensagem Mensagem descritiva da regra de negócio ou do resultado.
     * @return exceção configurada com o status e a mensagem correspondentes.
     */
    private ResponseStatusException naoEncontrado(String mensagem) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, mensagem);
    }
}