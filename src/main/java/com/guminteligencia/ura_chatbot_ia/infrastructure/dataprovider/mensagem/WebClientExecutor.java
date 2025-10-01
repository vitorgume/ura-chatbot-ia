package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.mensagem;

import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;
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
            WebClient.RequestHeadersSpec<?> requestSpec;

            WebClient.RequestBodyUriSpec baseRequest = webClient.method(method);

            WebClient.RequestBodySpec bodySpec = baseRequest
                    .uri(uri)
                    .headers(httpHeaders -> headers.forEach(httpHeaders::add));

            if (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH) {
                requestSpec = bodySpec.bodyValue(body);
            } else {
                requestSpec = bodySpec;
            }

            String response = requestSpec
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(retrySpec) // <- usa o spec injetado
                    .doOnError(e -> log.error("{} | Erro: {}", errorMessage, e.getMessage()))
                    .block();

            log.info("Response recebido: {}", response);
            return response;
        } catch (Exception e) {
            log.error(errorMessage, e);
            throw new DataProviderException(errorMessage, e.getCause());
        }
    }
}
