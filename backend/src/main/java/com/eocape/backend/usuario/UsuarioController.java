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

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody CadastroUsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.cadastrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(usuarioService.login(request));
    }

    @PostMapping("/definir-senha")
    public ResponseEntity<LoginResponse> definirSenha(
            @AuthenticationPrincipal UUID usuarioId,
            @Valid @RequestBody DefinirSenhaRequest request) {
        return ResponseEntity.ok(usuarioService.definirSenha(usuarioId, request));
    }

    @PostMapping("/trocar-senha")
    public ResponseEntity<Void> trocarSenha(
            @AuthenticationPrincipal UUID usuarioId,
            @Valid @RequestBody TrocaSenhaRequest request) {
        usuarioService.trocarSenha(usuarioId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @GetMapping
    public PagedModel<UsuarioResponse> listar(@PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return usuarioService.listar(pageable);
    }
}