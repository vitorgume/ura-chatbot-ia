package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.OutroContatoEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OutroContatoMapperTest {

    private OutroContatoEntity outroContatoEntity;

    @BeforeEach
    void setUp() {
        outroContatoEntity = OutroContatoEntity.builder()
                .id(1L)
                .nome("Nome outro contato")
                .telefone("000000000000")
                .descricao("Descrição domain")
                .build();
    }

    @Test
    void deveTransformaraParaDomain() {
        OutroContato outroContatoTeste = OutroContatoMapper.paraDomain(outroContatoEntity);

        Assertions.assertEquals(outroContatoTeste.getId(), outroContatoEntity.getId());
        Assertions.assertEquals(outroContatoTeste.getNome(), outroContatoEntity.getNome());
        Assertions.assertEquals(outroContatoTeste.getTelefone(), outroContatoEntity.getTelefone());
        Assertions.assertEquals(outroContatoTeste.getDescricao(), outroContatoEntity.getDescricao());
    }
}