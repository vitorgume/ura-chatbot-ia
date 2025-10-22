package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.MidiaCliente;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.MidiaClienteEntity;

public class MidiaClienteMapper {

    public static MidiaCliente paraDomain(MidiaClienteEntity entity) {
        return MidiaCliente.builder()
                .id(entity.getId())
                .telefoneCliente(entity.getTelefoneCliente())
                .urlMidias(entity.getUrlMidias())
                .build();
    }
}
