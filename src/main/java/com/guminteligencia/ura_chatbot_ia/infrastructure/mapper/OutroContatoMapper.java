package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.OutroContatoEntity;

public class OutroContatoMapper {
    public static OutroContato paraDomain(OutroContatoEntity entity) {
        return OutroContato.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .telefone(entity.getTelefone())
                .descricao(entity.getDescricao())
                .build();
    }
}
