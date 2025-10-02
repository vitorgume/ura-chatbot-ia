package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Chat;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ChatDto;

public class ChatMapper {

    public static ChatDto paraDto(Chat domain) {
        return ChatDto.builder()
                .id(domain.getId())
                .dataCriacao(domain.getDataCriacao())
                .cliente(ClienteMapper.paraDto(domain.getCliente()))
                .mensagensChat(domain.getMensagensChat().stream().map(MensagemConversaMapper::paraDto).toList())
                .build();
    }
}
