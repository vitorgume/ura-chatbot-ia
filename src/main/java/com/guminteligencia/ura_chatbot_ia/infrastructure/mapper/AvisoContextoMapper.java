package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guminteligencia.ura_chatbot_ia.domain.AvisoContexto;
import software.amazon.awssdk.services.sqs.model.Message;

public class AvisoContextoMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static AvisoContexto paraDomainDeMessage(Message message) {
        try {
            AvisoContexto avisoContexto = objectMapper.readValue(message.body(), AvisoContexto.class);
            avisoContexto.setMensagemFila(message);
            return avisoContexto;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter mensagem da fila para AvisoContexto", e);
        }
    }
}
