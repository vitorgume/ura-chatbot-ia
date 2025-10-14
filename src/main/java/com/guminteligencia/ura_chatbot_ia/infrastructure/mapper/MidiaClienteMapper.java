package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.MidiaCliente;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.MidiaCLienteEntity;

public class MidiaClienteMapper {

    public static MidiaCliente paraDomain(MidiaCLienteEntity entity) {
        return MidiaCliente.builder()
                .id(entity.getId())
                .telefoneCliente(entity.getTelefoneCliente())
                .urlMidias(entity.getUrlMidias())
                .build();
    }
}
