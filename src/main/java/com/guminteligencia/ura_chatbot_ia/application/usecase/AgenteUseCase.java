package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.guminteligencia.ura_chatbot_ia.application.gateways.AgenteGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.MensagemAgenteDto;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.MensagemContexto;
import com.guminteligencia.ura_chatbot_ia.domain.Qualificacao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgenteUseCase {

    private final AgenteGateway gateway;
    private static final ObjectMapper mapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    public String enviarMensagem(Cliente clienteSalvo, ConversaAgente conversa, List<MensagemContexto> mensagens) {
        log.info("Enviando mensagem para o agente. Cliente: {}, ConversaAgente: {}, Mensagens: {}", clienteSalvo, conversa, mensagens);

        MensagemAgenteDto mensagem = MensagemAgenteDto.builder()
                .clienteId(clienteSalvo.getId().toString())
                .conversaId(conversa.getId().toString())
                .mensagem(this.concatenarMensagens(mensagens))
                .audiosUrl(mensagens.stream().map(MensagemContexto::getAudioUrl).toList())
                .imagensUrl(mensagens.stream().map(MensagemContexto::getImagemUrl).toList())
                .build();

        String resposta = gateway.enviarMensagem(mensagem);

        log.info("Mensagem enviada com sucesso para o agente. Resposta: {}", resposta);

        return resposta;
    }

    public Qualificacao enviarJsonTrasformacao(String texto) {
        log.info("Enviando texto para ser transformado em JSON. Texto: {}", texto);

        String response = gateway.enviarJsonTrasformacao(texto);

        Qualificacao qualificacao = parseJson(response);

        log.info("Texto para transforma em JSON enviado com sucesso. JSON: {}", qualificacao);

        return qualificacao;
    }

    private String concatenarMensagens(List<MensagemContexto> mensagens) {
        return mensagens.stream().map(MensagemContexto::getMensagem).collect(Collectors.joining(", "));
    }

    private static Qualificacao parseJson(String json) {
        try {
            JsonNode node = mapper.readTree(json);

            if (node.isTextual()) {
                node = mapper.readTree(node.asText());
            }

            return mapper.treeToValue(node, Qualificacao.class);
        } catch (Exception e) {
            log.error("Falha ao parsear. json='{}'", json, e);
            throw new RuntimeException("Erro ao tentar mapear JSON da IA", e);
        }
    }
}
