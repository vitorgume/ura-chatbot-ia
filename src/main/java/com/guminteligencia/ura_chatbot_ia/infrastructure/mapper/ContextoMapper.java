package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ContextoEntity;
import software.amazon.awssdk.services.sqs.model.Message;
import com.fasterxml.jackson.databind.DeserializationFeature;

public class ContextoMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static Contexto paraDomain(ContextoEntity entity) {
        return Contexto.builder()
                .id(entity.getId())
                .mensagens(entity.getMensagens())
                .telefone(entity.getTelefone())
                .build();
    }

    public static ContextoEntity paraEntity(Contexto domain) {
        return ContextoEntity.builder()
                .id(domain.getId())
                .mensagens(domain.getMensagens())
                .telefone(domain.getTelefone())
                .build();
    }

    public static Contexto paraDomainDeMessage(Message message) {
        try {
            Contexto contexto = objectMapper.readValue(message.body(), Contexto.class);
            contexto.setMensagemFila(message);
            return contexto;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter mensagem da fila para Contexto", e);
        }
    }
}
