package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.AgenteGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.MensagemAgenteDto;
import com.guminteligencia.ura_chatbot_ia.domain.RespostaAgente;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class AgenteDataProvider implements AgenteGateway {

    private final WebClient webClient;

    @Value("${agente.ura.uri}")
    private final String agenteUriApi;

    private final String MENSAGEM_ERRO_ENVIAR_MENSAGEM_AGENTE = "Erro ao enviar mensagem para o agente.";

    public AgenteDataProvider(
            WebClient webClient,
            @Value("${agente.ura.uri}") String agenteUriApi
    ) {
        this.webClient = webClient;
        this.agenteUriApi = agenteUriApi;
    }

    @Override
    public RespostaAgente enviarMensagem(MensagemAgenteDto mensagem) {

        Map<String, Object> requestBody = Map.of(
                "cliente_id", mensagem.getClienteId(),
                "conversa_id", mensagem.getConversaId(),
                "message", mensagem.getMensagem()
        );


        String uri = agenteUriApi + "/chat";

        return webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(RespostaAgente.class)
                .retryWhen(
                        Retry.backoff(3, Duration.ofSeconds(2))
                                .filter(throwable -> {
                                    log.warn("Tentando novamente após erro: {}", throwable.getMessage());
                                    return true;
                                })
                )
                .doOnError(e -> log.error("{} | Erro: {}", MENSAGEM_ERRO_ENVIAR_MENSAGEM_AGENTE, e.getMessage()))
                .block();
    }
}
