package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.mensagem;

import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebClientExecutor {

    private final WebClient webClient;
    private final RetryBackoffSpec retrySpec;

    public String post(String uri, Object body, Map<String, String> headers, String errorMessage) {
        return execute(uri, body, headers, errorMessage, HttpMethod.POST);
    }

    public String execute(String uri, Object body, Map<String, String> headers, String errorMessage, HttpMethod method) {
        try {
            WebClient.RequestBodyUriSpec base = webClient.method(method);

            WebClient.RequestHeadersSpec<?> req = base
                    .uri(uri)
                    // garante Content-Type se não vier no bean
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .headers(h -> headers.forEach(h::add));

            if (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH) {
                req = ((WebClient.RequestBodySpec) req).bodyValue(body);
            }

            String response = req
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, resp ->
                            resp.bodyToMono(String.class)
                                    .defaultIfEmpty("<empty-body>")
                                    .flatMap(bodyStr -> {
                                        String msg = "%s | HTTP %d | Body: %s"
                                                .formatted(errorMessage, resp.statusCode().value(), bodyStr);
                                        log.error(msg);
                                        return Mono.error(new DataProviderException(msg, null));
                                    })
                    )
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30))
                    .retryWhen(retrySpec)
                    .doOnSuccess(r -> log.info("Response recebido: {}", r))
                    .block();

            return response;

        } catch (Exception e) {
            // Inclui a mensagem original (com status/body se veio do onStatus)
            String msg = "%s | cause=%s".formatted(errorMessage, e.getMessage());
            log.error(msg, e);
            throw new DataProviderException(msg, e);
        }
    }
}
