package com.eocape.backend.ata;

import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.ata.dto.AtaResponse;
import com.eocape.backend.ata.dto.CriarAtaRequest;
import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.condominio.CondominioRepository;
import com.eocape.backend.usuario.Usuario;
import com.eocape.backend.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AtaService {

    private final AtaRepository ataRepository;
    private final CondominioRepository condominioRepository;
    private final UsuarioRepository usuarioRepository;
    private final AcessoCondominioService acesso;

    @Transactional(readOnly = true)
    public List<AtaResponse> listar(UUID condominioId, UUID usuarioId) {
        acesso.exigirVinculo(usuarioId, condominioId);

        List<Ata> atas = acesso.isGestor(usuarioId, condominioId)
                ? ataRepository.findByCondominioIdOrderByDataReuniaoDesc(condominioId)
                : ataRepository.findByCondominioIdAndPublicadaTrueOrderByDataReuniaoDesc(condominioId);

        return atas.stream().map(AtaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AtaResponse buscar(UUID condominioId, UUID ataId, UUID usuarioId) {
        acesso.exigirVinculo(usuarioId, condominioId);

        Ata ata = ataRepository.findByIdAndCondominioId(ataId, condominioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ata não encontrada"));

        // Morador não vê rascunho: responde como se a ata não existisse
        if (!ata.isPublicada() && !acesso.isGestor(usuarioId, condominioId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ata não encontrada");
        }

        return AtaResponse.from(ata);
    }

    @Transactional
    public AtaResponse criar(UUID condominioId, UUID usuarioId, CriarAtaRequest request) {
        acesso.exigirGestor(usuarioId, condominioId);

        Condominio condominio = condominioRepository.findById(condominioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Condomínio não encontrado"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        Ata ata = new Ata(condominio, usuario, request.titulo(), request.dataReuniao(), request.resumo());
        if (request.publicada()) {
            ata.publicar();
        }

        return AtaResponse.from(ataRepository.save(ata));
    }

    @Transactional
    public AtaResponse publicar(UUID condominioId, UUID ataId, UUID usuarioId) {
        acesso.exigirGestor(usuarioId, condominioId);

        Ata ata = ataRepository.findByIdAndCondominioId(ataId, condominioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ata não encontrada"));

        ata.publicar();
        return AtaResponse.from(ata);
    }
}