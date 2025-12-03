package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.guminteligencia.ura_chatbot_ia.domain.AvisoContexto;
import com.fasterxml.jackson.databind.JsonNode;
import software.amazon.awssdk.services.sqs.model.Message;

public class AvisoContextoMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static AvisoContexto paraDomainDeMessage(Message message) {
        try {
            String body = message.body();

            JsonNode root = objectMapper.readTree(body);
            if (root.hasNonNull("Message")) {
                body = root.get("Message").asText();
            }

            AvisoContexto avisoContexto = objectMapper.readValue(body, AvisoContexto.class);
            avisoContexto.setMensagemFila(message);
            return avisoContexto;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter mensagem da fila para AvisoContexto", e);
        }
    }
}
