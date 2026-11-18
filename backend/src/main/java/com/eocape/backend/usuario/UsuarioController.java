package com.eocape.backend.usuario;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import com.eocape.backend.usuario.dto.request.CadastroUsuarioRequest;
import com.eocape.backend.usuario.dto.request.DefinirSenhaRequest;
import com.eocape.backend.usuario.dto.request.LoginRequest;
import com.eocape.backend.usuario.dto.request.TrocaSenhaRequest;
import com.eocape.backend.usuario.dto.response.LoginResponse;
import com.eocape.backend.usuario.dto.response.UsuarioResponse;

/**
 * Expõe os endpoints HTTP de usuários e delega o processamento à camada de serviço.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    /**
     * Serviço responsável por usuários.
     */
    private final UsuarioService usuarioService;

    /**
     * Inicializa UsuarioController com os dados e as dependências necessários à operação.
     *
     * @param usuarioService Serviço responsável por usuários.
     */
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Cadastra usuários.
     *
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws org.springframework.web.server.ResponseStatusException se a regra de negócio ou o estado atual não permitir a operação.
     */
    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody CadastroUsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.cadastrar(request));
    }

    /**
     * Autentica o usuário por CPF e senha e emite um token adequado ao estágio do primeiro acesso.
     *
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws org.springframework.web.server.ResponseStatusException se ocorrer a condição: CPF ou senha inválidos.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(usuarioService.login(request));
    }

    /**
     * Conclui o primeiro acesso com a nova senha e emite o token de acesso.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws org.springframework.web.server.ResponseStatusException se a regra de negócio ou o estado atual não permitir a operação.
     */
    @PostMapping("/definir-senha")
    public ResponseEntity<LoginResponse> definirSenha(
            @AuthenticationPrincipal UUID usuarioId,
            @Valid @RequestBody DefinirSenhaRequest request) {
        return ResponseEntity.ok(usuarioService.definirSenha(usuarioId, request));
    }

    /**
     * Confirma a senha atual e substitui o hash pela nova senha informada.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws org.springframework.web.server.ResponseStatusException se ocorrer a condição: Senha atual incorreta.
     */
    @PostMapping("/trocar-senha")
    public ResponseEntity<Void> trocarSenha(
            @AuthenticationPrincipal UUID usuarioId,
            @Valid @RequestBody TrocaSenhaRequest request) {
        usuarioService.trocarSenha(usuarioId, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Busca pelo identificador usuários.
     *
     * @param id Identificador único do registro.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws org.springframework.web.server.ResponseStatusException se ocorrer a condição: Usuário não encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    /**
     * Lista usuários.
     *
     * @param pageable Configuração de paginação e ordenação da consulta.
     * @return página de resultados com os dados e as informações de paginação.
     */
    @GetMapping
    public PagedModel<UsuarioResponse> listar(@PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return usuarioService.listar(pageable);
    }
}