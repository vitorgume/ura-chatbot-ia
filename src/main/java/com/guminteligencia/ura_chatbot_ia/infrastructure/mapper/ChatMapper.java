package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Chat;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ChatEntity;

public class ChatMapper {

    public static Chat paraDomain(ChatEntity entity) {
         return Chat.builder()
                 .id(entity.getId())
                 .dataCriacao(entity.getDataCriacao())
                 .cliente(ClienteMapper.paraDomain(entity.getCliente()))
                 .mensagensChat(entity.getMensagensChat().stream().map(MensagemConversaMapper::paraDomain).toList())
                 .build();
    }

    public static ChatEntity paraEntity(Chat domain) {
        return ChatEntity.builder()
                .id(domain.getId())
                .dataCriacao(domain.getDataCriacao())
                .cliente(ClienteMapper.paraEntity(domain.getCliente()))
                .mensagensChat(domain.getMensagensChat().stream().map(MensagemConversaMapper::paraEntity).toList())
                .build();
    }
}
