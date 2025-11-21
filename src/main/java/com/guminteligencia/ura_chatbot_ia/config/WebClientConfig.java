package com.guminteligencia.ura_chatbot_ia.config;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .responseTimeout(Duration.ofSeconds(30))
                        .doOnConnected(conn -> conn
                                .addHandlerLast(new ReadTimeoutHandler(30))
                                .addHandlerLast(new WriteTimeoutHandler(30)))))
                .build();
    }

    @Bean
    public RetryBackoffSpec retrySpec() {
        return Retry
                .backoff(/*maxRetries*/ 3, Duration.ofMillis(200))
                .maxBackoff(Duration.ofSeconds(2))
                // evita retry em 4xx (normalmente não adianta repetir)
                .filter(ex -> !(ex instanceof WebClientResponseException wcre
                        && wcre.getStatusCode().is4xxClientError()))
                // se esgotar as tentativas, propaga a última exceção
                .onRetryExhaustedThrow((spec, signal) -> signal.failure());
    }
}
