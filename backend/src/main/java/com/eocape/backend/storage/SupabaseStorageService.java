package com.eocape.backend.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Envia arquivos ao bucket privado e obtém URLs assinadas para download no Supabase.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Service
@RequiredArgsConstructor
public class SupabaseStorageService {

    /**
     * Cliente HTTP utilizado para comunicação com a API do Supabase.
     */
    private final RestClient restClient = RestClient.create();

    /**
     * Endereço base da API do projeto Supabase.
     */
    @Value("${app.supabase.url}")
    private String supabaseUrl;

    /**
     * Credencial de serviço do armazenamento; não deve ser exposta ao cliente.
     */
    @Value("${app.supabase.service-key}")
    private String serviceKey;

    /**
     * Nome do bucket privado de armazenamento.
     */
    @Value("${app.supabase.bucket}")
    private String bucket;

    /**
     * Envia o conteúdo ao bucket privado.
     *
     * @param chave Chave utilizada na assinatura de tokens ou na identificação do objeto armazenado, conforme o tipo.
     * @param conteudo Conteúdo textual do comunicado ou bytes do arquivo, conforme o tipo declarado.
     * @param tipoMime Tipo MIME do conteúdo armazenado.
     */
    public void enviar(String chave, byte[] conteudo, String tipoMime) {
        String url = supabaseUrl + "/storage/v1/object/" + bucket + "/" + chave;

        restClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + serviceKey)
                .header("apikey", serviceKey)
                .contentType(MediaType.parseMediaType(tipoMime))
                .body(conteudo)
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * Gera URL de download válida por tempo limitado.
     *
     * @param chave Chave utilizada na assinatura de tokens ou na identificação do objeto armazenado, conforme o tipo.
     * @param segundos Prazo de validade da URL assinada, expresso em segundos.
     * @return URL assinada que permite baixar o objeto durante seu prazo de validade.
     */
    public String gerarUrlAssinada(String chave, int segundos) {
        String url = supabaseUrl + "/storage/v1/object/sign/" + bucket + "/" + chave;

        Map<?, ?> resposta = restClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + serviceKey)
                .header("apikey", serviceKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("expiresIn", segundos))
                .retrieve()
                .body(Map.class);

        String assinado = (String) resposta.get("signedURL");
        return supabaseUrl + "/storage/v1" + assinado;
    }

    /**
     * Remove o objeto do bucket.
     *
     * @param chave Chave utilizada na assinatura de tokens ou na identificação do objeto armazenado, conforme o tipo.
     */
    public void remover(String chave) {
        String url = supabaseUrl + "/storage/v1/object/" + bucket + "/" + chave;

        restClient.delete()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + serviceKey)
                .header("apikey", serviceKey)
                .retrieve()
                .toBodilessEntity();
    }
}