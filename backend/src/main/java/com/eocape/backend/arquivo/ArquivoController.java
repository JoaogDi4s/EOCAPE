package com.eocape.backend.arquivo;

import com.eocape.backend.arquivo.dto.ArquivoResponse;
import com.eocape.backend.arquivo.dto.RegistrarArquivoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Expõe os endpoints HTTP de arquivos e delega o processamento à camada de serviço.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/arquivo")
@RequiredArgsConstructor
public class ArquivoController {

    /**
     * Serviço responsável por arquivos.
     */
    private final ArquivoService service;

    /**
     * Registra arquivos.
     *
     * @param request Dados de entrada recebidos e validados para a operação.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Condomínio não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @PostMapping
    public ResponseEntity<ArquivoResponse> registrar(@Valid @RequestBody RegistrarArquivoRequest request,
                                                     @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(solicitanteId, request));
    }

    /**
     * Valida e envia o conteúdo de arquivos.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param tipo Tipo do registro, conforme os valores admitidos pelo domínio.
     * @param arquivo Conteúdo binário e metadados do arquivo recebido no upload.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Condomínio não encontrado: .
     * @throws IllegalArgumentException se ocorrer a condição: Tipo do arquivo não identificado.
     * @throws IllegalStateException se ocorrer a condição: Falha ao ler o arquivo enviado.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<ArquivoResponse> upload(@RequestParam UUID condominioId,
                                                  @RequestParam TipoArquivo tipo,
                                                  @RequestParam("arquivo") MultipartFile arquivo,
                                                  @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.upload(solicitanteId, condominioId, tipo, arquivo));
    }

    /**
     * Lista arquivos.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @GetMapping("/condominio/{condominioId}")
    public ResponseEntity<List<ArquivoResponse>> listar(@PathVariable UUID condominioId,
                                                        @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.listar(solicitanteId, condominioId));
    }

    /**
     * Busca pelo identificador arquivos.
     *
     * @param id Identificador único do registro.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Você só pode visualizar os seus arquivos.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Arquivo não encontrado: .
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArquivoResponse> buscarPorId(@PathVariable UUID id,
                                                       @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(service.buscarPorId(solicitanteId, id));
    }

    /**
     * Autoriza e disponibiliza o download de arquivos.
     *
     * @param id Identificador único do registro.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return resposta HTTP com o status da operação e o conteúdo previsto pelo endpoint.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Este arquivo não está disponível para o seu acesso.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Arquivo não encontrado: .
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<Map<String, String>> download(@PathVariable UUID id,
                                                        @AuthenticationPrincipal UUID solicitanteId) {
        return ResponseEntity.ok(Map.of("url", service.gerarUrlDownload(solicitanteId, id)));
    }
}