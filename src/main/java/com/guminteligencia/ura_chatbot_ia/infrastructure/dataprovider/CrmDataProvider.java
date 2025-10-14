package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.fasterxml.jackson.databind.JsonNode;
import com.guminteligencia.ura_chatbot_ia.application.gateways.CrmGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.CardDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.SessaoArquivoDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.UploadParteRespostaDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactsResponse;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.RemoteFileMetaDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

@Component
@Slf4j
public class CrmDataProvider implements CrmGateway {


    private final WebClient webClient;

    public CrmDataProvider(@Qualifier("kommoWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    private static final String MENSAGEM_ERRO_CONSULTAR_LEAD_PELO_TELEFONE = "Erro ao consultar lead pelo seu telefone.";
    private static final String MENSAGEM_ERRO_ATUALIZAR_CARD = "Erro ao atualizar card.";
    private static final String MENSAGEM_ERRO_CRIAR_SESSAO_ARQUIVO = "Erro ao criar sessão arquivo.";

    public Optional<Integer> consultaLeadPeloTelefone(String telefoneE164) {
        String normalized = normalizeE164(telefoneE164);

        Integer leadId;

        try {
            ContactsResponse contacts = webClient.get()
                    .uri(uri -> uri.path("/contacts")
                            .queryParam("query", normalized)
                            .queryParam("with", "leads")
                            .queryParam("limit", 50)
                            .build())
                    .retrieve()
                    .bodyToMono(ContactsResponse.class)
                    .block();

            if (contacts == null || contacts.getEmbedded() == null || contacts.getEmbedded().getContacts() == null) {
                return Optional.empty();
            }

            var contato = contacts.getEmbedded().getContacts().stream()
                    .filter(c -> c.getEmbedded() != null && c.getEmbedded().getLeads() != null && !c.getEmbedded().getLeads().isEmpty())
                    .max(Comparator.comparing(ContactDto::getUpdatedAt, Comparator.nullsFirst(Long::compareTo)))
                    .orElse(null);

            if (contato == null) return Optional.empty();

            leadId = contato.getEmbedded().getLeads().get(0).getId();
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_LEAD_PELO_TELEFONE, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_LEAD_PELO_TELEFONE, ex.getCause());
        }

        return Optional.ofNullable(leadId);
    }

    @Override
    public void atualizarCard(CardDto body, Integer idLead) {
        System.out.println("Body: " + body);
        try {
            webClient.patch()
                    .uri(uri -> uri.path("/leads/{id}").build(idLead))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_ATUALIZAR_CARD, ex);
            throw new DataProviderException(MENSAGEM_ERRO_ATUALIZAR_CARD, ex.getCause());
        }
    }

    @Override
    public SessaoArquivoDto criarSessaoArquivo() {
        try {
            return webClient.post()
                    .uri("https://drive-c.kommo.com/v1.0/sessions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(java.util.Collections.emptyMap())
                    .retrieve()
                    .bodyToMono(SessaoArquivoDto.class)
                    .block();
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CRIAR_SESSAO_ARQUIVO, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CRIAR_SESSAO_ARQUIVO, ex.getCause());
        }
    }

    @Override
    public String enviarArquivoParaUpload(SessaoArquivoDto sessaoArquivo, String urlArquivo) {

            RemoteFileMetaDto meta = obterMeta(urlArquivo);

            if (sessaoArquivo.getMaxFileSize() != null && meta.getLength() > 0 && meta.getLength() > sessaoArquivo.getMaxFileSize()) {
                throw new IllegalArgumentException("Arquivo excede max_file_size do Kommo.");
            }

            if (meta.getLength() > 0 && sessaoArquivo.getMaxPartSize() != null && meta.getLength() <= sessaoArquivo.getMaxPartSize()) {
                byte[] bytes = baixarBytes(urlArquivo);
                UploadParteRespostaDto r = postChunk(sessaoArquivo.getUploadUrl(), bytes, 0, bytes.length - 1, meta.getLength(), meta.getContentType());
                if (!r.isFinished() && r.getFileUuid() == null) {

                    log.warn("Upload em parte única não retornou finished; nextUrl={}", r.getNextUploadUrl());
                }
                if (r.getFileUuid() == null) {
                    throw new IllegalStateException("Upload concluído, mas sem file_uuid na resposta.");
                }

                return r.getFileUuid();
            }

            long total = meta.getLength();
            long offset = 0;
            String nextUrl = sessaoArquivo.getUploadUrl();

            try (InputStream in = new BufferedInputStream(new URL(urlArquivo).openStream())) {
                int chunkSize = (sessaoArquivo.getMaxPartSize() != null ? sessaoArquivo.getMaxPartSize() : 5 * 1024 * 1024);
                byte[] buf = new byte[chunkSize];

                while (true) {
                    int read = in.read(buf);
                    if (read == -1) break;

                    long start = offset;
                    long end = offset + read - 1;
                    byte[] slice = (read == buf.length) ? buf : Arrays.copyOf(buf, read);

                    UploadParteRespostaDto r = postChunk(nextUrl, slice, start, end, total, meta.getContentType());
                    offset += read;

                    if (r.getNextUploadUrl() != null) nextUrl = r.getNextUploadUrl();
                    if (r.isFinished()) {
                        if (r.getFileUuid() == null)
                            throw new IllegalStateException("Upload finalizado sem file_uuid.");
                        return r.getFileUuid();
                    }
                }
            } catch (IOException e) {
                throw new DataProviderException("Falha no streaming do arquivo para o Kommo.", e);
            }

            throw new IllegalStateException("Upload não finalizado.");
    }

    private UploadParteRespostaDto postChunk(String uploadUrl,
                                             byte[] bytes,
                                             long start, long end, long total,
                                             String contentType) {
        WebClient.RequestBodySpec spec = webClient.post().uri(uploadUrl);

        if (total > 0) {
            String range = String.format("bytes %d-%d/%d", start, end, total);
            spec = spec.header("Content-Range", range);
        }
        if (contentType != null && !contentType.isBlank()) {
            spec = spec.contentType(MediaType.parseMediaType(contentType));
        } else {
            spec = spec.contentType(MediaType.APPLICATION_OCTET_STREAM);
        }

        JsonNode node = spec
                .bodyValue(bytes) // envia bytes “puros”
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        UploadParteRespostaDto resposta = parseUploadResponse(node);
        log.debug("Chunk enviado: start={}, end={}, finished={}, nextUrl={}",
                start, end, resposta.isFinished(), resposta.getNextUploadUrl());
        return resposta;
    }

    private static byte[] baixarBytes(String url) {
        try {
            return WebClient.create()
                    .get().uri(url)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
        } catch (Exception e) {
            throw new DataProviderException("Falha ao baixar arquivo de origem.", e);
        }
    }

    private RemoteFileMetaDto obterMeta(String url) {
        try {
            var resp = WebClient.create()
                    .method(HttpMethod.HEAD).uri(url)
                    .exchangeToMono(r -> Mono.just(Tuples.of(r.headers().asHttpHeaders(), r.statusCode())))
                    .block();

            long len = -1;
            String ct = null;
            if (resp != null) {
                var headers = resp.getT1();
                ct = headers.getFirst(HttpHeaders.CONTENT_TYPE);
                String cl = headers.getFirst(HttpHeaders.CONTENT_LENGTH);
                if (cl != null) len = Long.parseLong(cl);
            }

            // Se o servidor não suportar HEAD/Content-Length, tentamos via GET dos headers
            if (len <= 0 || ct == null) {
                var getResp = WebClient.create().get().uri(url).exchangeToMono(r ->
                        r.releaseBody().then(Mono.just(r.headers().asHttpHeaders()))
                ).block();
                if (getResp != null) {
                    if (ct == null) ct = getResp.getFirst(HttpHeaders.CONTENT_TYPE);
                    if (len <= 0) {
                        String cl = getResp.getFirst(HttpHeaders.CONTENT_LENGTH);
                        if (cl != null) len = Long.parseLong(cl);
                    }
                }
            }
            return new RemoteFileMetaDto(len, ct);
        } catch (Exception e) {
            // não é crítico; seguimos sem meta
            return new RemoteFileMetaDto(-1, null);
        }
    }

    private UploadParteRespostaDto parseUploadResponse(JsonNode root) {
        // os nomes podem variar; tente cobrir os mais comuns
        String nextUrl =
                root.path("next_url").asText(null);
        if (nextUrl == null) nextUrl = root.path("upload_url").asText(null);

        boolean finished =
                root.path("finished").asBoolean(false)
                        || root.path("is_finished").asBoolean(false)
                        || root.path("complete").asBoolean(false);

        String fileUuid =
                root.path("file_uuid").asText(null);
        if (fileUuid == null) fileUuid = root.path("uuid").asText(null);

        String versionUuid =
                root.path("version_uuid").asText(null);

        // heurística: se veio uuid mas não veio nextUrl, consideramos finalizado
        if (fileUuid != null && nextUrl == null) finished = true;

        return UploadParteRespostaDto.builder()
                .finished(finished)
                .nextUploadUrl(nextUrl)
                .fileUuid(fileUuid)
                .versionUuid(versionUuid)
                .build();
    }

    private static String normalizeE164(String fone) {
        String f = fone == null ? "" : fone.trim();
        f = f.replaceAll("[^\\d+]", "");
        if (!f.startsWith("+")) f = "+" + f;
        return f;
    }
}
