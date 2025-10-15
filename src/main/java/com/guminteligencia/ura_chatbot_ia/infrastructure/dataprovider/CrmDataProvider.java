package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.fasterxml.jackson.databind.JsonNode;
import com.guminteligencia.ura_chatbot_ia.application.gateways.CrmGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.CardDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.SessaoArquivoDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.UploadParteRespostaDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactsResponse;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.KommoSessionRequest;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.RemoteFileMetaDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
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
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
@Slf4j
public class CrmDataProvider implements CrmGateway {


    private static final String KOMMO_DRIVE_BASE = "https://drive-c.kommo.com"; // ajuste para o drive da sua conta, se necessário

    private final WebClient webClient;

    public CrmDataProvider(@Qualifier("kommoWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    private static final String MENSAGEM_ERRO_CONSULTAR_LEAD_PELO_TELEFONE = "Erro ao consultar lead pelo seu telefone.";
    private static final String MENSAGEM_ERRO_ATUALIZAR_CARD = "Erro ao atualizar card.";
    private static final String MENSAGEM_ERRO_CRIAR_SESSAO_ARQUIVO = "Erro ao criar sessão arquivo.";

    // ========= Helpers para arquivo local =========

    private static boolean isLocalFile(String s) {
        if (s == null || s.isBlank()) return false;
        return s.startsWith("file:") || !(s.startsWith("http://") || s.startsWith("https://"));
    }

    private static Path toPath(String s) {
        return s.startsWith("file:") ? Paths.get(URI.create(s)) : Paths.get(s);
    }

    private static String guessContentType(Path p) {
        try {
            String ct = Files.probeContentType(p);
            if (ct != null) return ct;
        } catch (Exception ignore) {
        }
        String guess = URLConnection.guessContentTypeFromName(p.getFileName().toString());
        return guess != null ? guess : "application/octet-stream";
    }

    private static String extractFilename(String urlOrPath) {
        try {
            if (isLocalFile(urlOrPath)) {
                return toPath(urlOrPath).getFileName().toString();
            } else {
                URI u = URI.create(urlOrPath);
                String path = u.getPath();
                if (path == null || path.isBlank()) return "upload.bin";
                int idx = path.lastIndexOf('/');
                return (idx >= 0 && idx < path.length() - 1) ? path.substring(idx + 1) : "upload.bin";
            }
        } catch (Exception e) {
            return "upload.bin";
        }
    }

    // ==============================================

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
    public SessaoArquivoDto criarSessaoArquivo(String urlArquivo) {
        try {
            long size = detectarTamanhoReal(urlArquivo);
            if (size <= 0) {
                throw new DataProviderException("Falha ao determinar tamanho real do arquivo: " + urlArquivo, null);
            }

            String contentType = isLocalFile(urlArquivo)
                    ? guessContentType(toPath(urlArquivo))
                    : Optional.ofNullable(obterMeta(urlArquivo).getContentType()).orElse(null);

            if (contentType == null || contentType.isBlank()) contentType = "application/octet-stream";

            String filename = extractFilename(urlArquivo);

            log.info("🪶 Criando sessão Kommo");
            log.info("📂 Arquivo: {}", filename);
            log.info("📏 file_size: {} bytes ({} KB)", size, size / 1024);
            log.info("🧾 Content-Type: {}", contentType);

            // body com nomes aceitos pelo Drive (file_name, file_size, content_type)
            var body = new KommoSessionRequest(filename, size, contentType);

            SessaoArquivoDto sessao = webClient.post()
                    .uri(KOMMO_DRIVE_BASE + "/v1.0/sessions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(SessaoArquivoDto.class)
                    .block();

            if (sessao == null) {
                throw new DataProviderException("Resposta nula ao criar sessão de upload no Kommo.", null);
            }

            log.info("✅ Sessão criada: uploadUrl={} maxFileSize={} maxPartSize={}",
                    sessao.getUploadUrl(), sessao.getMaxFileSize(), sessao.getMaxPartSize());

            if (sessao.getMaxFileSize() != null && size > sessao.getMaxFileSize()) {
                throw new IllegalArgumentException("Arquivo excede max_file_size do Kommo.");
            }

            return sessao;
        } catch (Exception ex) {
            log.error("Erro ao criar sessão arquivo.", ex);
            throw new DataProviderException("Erro ao criar sessão arquivo.", ex.getCause());
        }
    }

    @Override
    public String enviarArquivoParaUpload(SessaoArquivoDto sessaoArquivo, String urlArquivo) {

        // Detecta tamanho real para decidir single vs multi-part com segurança
        long realSize = detectarTamanhoReal(urlArquivo);
        RemoteFileMetaDto meta = obterMeta(urlArquivo);
        if (meta.getLength() <= 0) {
            meta = new RemoteFileMetaDto(realSize, meta.getContentType());
        }

        if (sessaoArquivo.getMaxFileSize() != null && realSize > 0 && realSize > sessaoArquivo.getMaxFileSize()) {
            throw new IllegalArgumentException("Arquivo excede max_file_size do Kommo.");
        }

        String uploadUrl = sessaoArquivo.getUploadUrl();
        String contentType = Optional.ofNullable(meta.getContentType()).orElse("application/octet-stream");

        // SINGLE-PART: quando o arquivo total ≤ max_part_size
        if (realSize > 0 && sessaoArquivo.getMaxPartSize() != null && realSize <= sessaoArquivo.getMaxPartSize()) {
            byte[] bytes = baixarBytes(urlArquivo);
            UploadParteRespostaDto r = postChunk(uploadUrl, bytes, contentType);
            if (!r.isFinished() && r.getFileUuid() == null) {
                log.warn("Upload em parte única não retornou finished; nextUrl={}", r.getNextUploadUrl());
            }
            if (r.getFileUuid() == null) {
                throw new IllegalStateException("Upload concluído, mas sem file_uuid na resposta.");
            }
            return r.getFileUuid();
        }

        // MULTI-PART (sem Content-Range)
        long offset = 0;
        String nextUrl = uploadUrl;

        try (InputStream in = isLocalFile(urlArquivo)
                ? new BufferedInputStream(Files.newInputStream(toPath(urlArquivo)))
                : new BufferedInputStream(new URL(urlArquivo).openStream())) {

            int defaultChunk = 512 * 1024; // 512 KB
            int maxPart = (sessaoArquivo.getMaxPartSize() != null ? sessaoArquivo.getMaxPartSize() : defaultChunk);
            int chunkSize = Math.min(maxPart, 5 * 1024 * 1024); // segurança

            byte[] buf = new byte[chunkSize];

            while (true) {
                int read = in.read(buf);
                if (read == -1) break;

                byte[] slice = (read == buf.length) ? buf : Arrays.copyOf(buf, read);

                UploadParteRespostaDto r = postChunk(nextUrl, slice, contentType);
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

    private long detectarTamanhoReal(String urlArquivo) {
        try {
            if (isLocalFile(urlArquivo)) {
                Path path = toPath(urlArquivo);
                if (!Files.exists(path))
                    throw new IOException("Arquivo local não encontrado: " + path);
                long len = Files.size(path);
                log.info("📦 Tamanho local detectado: {} bytes ({} KB)", len, len / 1024);
                return len;
            }

            URL url = new URL(urlArquivo);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            long length = conn.getContentLengthLong();

            if (length > 0) {
                log.info("🌐 Tamanho remoto detectado via HEAD: {} bytes ({} KB)", length, length / 1024);
                return length;
            }

            // Fallback: baixa o arquivo inteiro (apenas se necessário)
            try (InputStream in = new BufferedInputStream(url.openStream())) {
                byte[] buffer = in.readAllBytes();
                long len = buffer.length;
                log.info("⬇️  Tamanho remoto detectado via download: {} bytes ({} KB)", len, len / 1024);
                return len;
            }
        } catch (Exception e) {
            log.warn("⚠️  Falha ao detectar tamanho real do arquivo {}: {}", urlArquivo, e.getMessage());
            return -1;
        }
    }

    private UploadParteRespostaDto postChunk(String uploadUrl,
                                             byte[] bytes,
                                             String contentType) {
        JsonNode node = webClient.post()
                .uri(uploadUrl)
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // chunk binário
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(bytes.length))
                .bodyValue(bytes)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        UploadParteRespostaDto r = parseUploadResponse(node);
        log.debug("Chunk enviado: len={}, finished={}, nextUrl={}",
                bytes.length, r.isFinished(), r.getNextUploadUrl());
        return r;
    }

    private static byte[] baixarBytes(String url) {
        try {
            if (isLocalFile(url)) {
                return Files.readAllBytes(toPath(url));
            }
            return WebClient.create()
                    .get().uri(URI.create(url))
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
        } catch (Exception e) {
            throw new DataProviderException("Falha ao baixar arquivo de origem.", e);
        }
    }

    private RemoteFileMetaDto obterMeta(String url) {
        try {
            if (isLocalFile(url)) {
                Path p = toPath(url);
                long len = Files.exists(p) ? Files.size(p) : -1;
                String ct = guessContentType(p);
                return new RemoteFileMetaDto(len, ct);
            }

            var resp = WebClient.create()
                    .method(HttpMethod.HEAD).uri(URI.create(url))
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

            // fallback via GET headers
            if (len <= 0 || ct == null) {
                var getResp = WebClient.create().get().uri(URI.create(url)).exchangeToMono(r ->
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
