package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.MensagemConversa;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.MensagemConversaDto;

public class MensagemConversaMapper {

    public static MensagemConversaDto paraDto(MensagemConversa domain) {
        return MensagemConversaDto.builder()
                .id(domain.getId())
                .responsavel(domain.getResponsavel())
                .conteudo(domain.getConteudo())
                .data(domain.getData())
                .build();
    }
}
