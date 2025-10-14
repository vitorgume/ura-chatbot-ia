package com.guminteligencia.ura_chatbot_ia.config;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;

@Configuration
public class KommoConfig {

    @Bean("kommoWebClient")
    public WebClient kommoWebClient(
            WebClient.Builder builder,
            @Value("${app.crm.url}") String baseUrl,
            @Value("${app.crm.access-token}") String accessToken
    ) {
        if (baseUrl == null || !baseUrl.startsWith("http")) {
            throw new IllegalArgumentException("app.crm.url inválido: " + baseUrl);
        }
        return builder
                .baseUrl(baseUrl)
                .defaultHeaders(h -> {
                    h.setBearerAuth(accessToken);
                    h.setAccept(List.of(MediaType.valueOf("application/hal+json")));
                })
                .filter(errorMappingFilter())
                .build();
    }

    private static ExchangeFilterFunction errorMappingFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(res -> {
            if (res.statusCode().isError()) {
                return res.bodyToMono(String.class)
                        .defaultIfEmpty("")
                        .flatMap(body -> Mono.error(new KommoApiException(res.statusCode(), body)));
            }
            return Mono.just(res);
        });
    }

    public static class KommoApiException extends RuntimeException {
        private final HttpStatusCode status;
        public KommoApiException(HttpStatusCode status, String body) {
            super("Kommo error " + status + " body=" + body);
            this.status = status;
        }
        public HttpStatusCode getStatus() { return status; }
    }
}
