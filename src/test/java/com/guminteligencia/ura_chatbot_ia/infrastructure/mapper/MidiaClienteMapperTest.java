package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.MidiaCliente;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.MidiaClienteEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

class MidiaClienteMapperTest {


    private MidiaClienteEntity midiaCLienteEntity;

    @BeforeEach
    void setUp() {
        midiaCLienteEntity = MidiaClienteEntity.builder()
                .id(UUID.randomUUID())
                .telefoneCliente("teste")
                .urlMidias(List.of("teste01", "teste02"))
                .build();
    }

    @Test
    void deveRetornarDomainComSucesso() {
        MidiaCliente midiaClienteDomain = MidiaClienteMapper.paraDomain(midiaCLienteEntity);

        Assertions.assertEquals(midiaClienteDomain.getId(), midiaCLienteEntity.getId());
        Assertions.assertEquals(midiaClienteDomain.getTelefoneCliente(), midiaCLienteEntity.getTelefoneCliente());
        Assertions.assertEquals(midiaClienteDomain.getUrlMidias().get(0), midiaCLienteEntity.getUrlMidias().get(0));
        Assertions.assertEquals(midiaClienteDomain.getUrlMidias().get(1), midiaCLienteEntity.getUrlMidias().get(1));
    }
}