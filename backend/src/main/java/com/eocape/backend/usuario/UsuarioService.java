package com.eocape.backend.usuario;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public UsuarioResponse cadastrar(CadastroUsuarioRequest request) {
        String cpfLimpo = request.cpf().replaceAll("\\D", "");

        if (usuarioRepository.existsByCpf(cpfLimpo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        }
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }

        Usuario usuario = new Usuario(cpfLimpo, request.nome(), request.email(), request.telefone());
        Usuario salvo = usuarioRepository.save(usuario);

        // Próximo passo: gerar o token de ativação e enviar o link por e-mail

        return UsuarioResponse.from(salvo);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        return UsuarioResponse.from(usuario);
    }

    @Transactional(readOnly = true)
    public PagedModel<UsuarioResponse> listar(Pageable pageable) {
        Page<UsuarioResponse> pagina = usuarioRepository.findAll(pageable)
                .map(UsuarioResponse::from);
        return new PagedModel<>(pagina);
    }
}