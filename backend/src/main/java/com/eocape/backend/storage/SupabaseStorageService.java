package com.eocape.backend.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SupabaseStorageService {

    private final RestClient restClient = RestClient.create();

    @Value("${app.supabase.url}")
    private String supabaseUrl;

    @Value("${app.supabase.service-key}")
    private String serviceKey;

    @Value("${app.supabase.bucket}")
    private String bucket;

    /** Envia o conteúdo ao bucket privado. */
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

    /** Gera URL de download válida por tempo limitado. */
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

    /** Remove o objeto do bucket. */
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