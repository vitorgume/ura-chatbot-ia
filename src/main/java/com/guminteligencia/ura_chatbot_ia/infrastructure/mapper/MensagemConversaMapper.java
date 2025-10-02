package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.MensagemConversa;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.MensagemConversaEntity;

public class MensagemConversaMapper {

    public static MensagemConversa paraDomain(MensagemConversaEntity entity) {
        return MensagemConversa.builder()
                .id(entity.getId())
                .responsavel(entity.getResponsavel())
                .conteudo(entity.getConteudo())
                .data(entity.getData())
                .conversaAgente(ConversaAgenteMapper.paraDomain(entity.getConversaAgente()))
                .build();
    }

    public static MensagemConversaEntity paraEntity(MensagemConversa domain) {
        return MensagemConversaEntity.builder()
                .id(domain.getId())
                .responsavel(domain.getResponsavel())
                .conteudo(domain.getConteudo())
                .data(domain.getData())
                .conversaAgente(ConversaAgenteMapper.paraEntity(domain.getConversaAgente()))
                .build();
    }
}
