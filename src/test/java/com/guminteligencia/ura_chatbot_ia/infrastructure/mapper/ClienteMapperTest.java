package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Administrador;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ClienteEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClienteMapperTest {

    private Cliente clienteDomain;
    private ClienteEntity clienteEntity;

    @BeforeEach
    void setUp() {
        clienteDomain = Cliente.builder()
                .id(UUID.randomUUID())
                .nome("Nome domain")
                .telefone("00000000000")
                .regiao(Regiao.MARINGA)
                .segmento(Segmento.ALIMENTOS)
                .inativo(false)
                .descricaoMaterial("Descrição domain")
                .build();

        clienteEntity = ClienteEntity.builder()
                .id(UUID.randomUUID())
                .nome("Nome entity")
                .telefone("000000000001")
                .regiao(Regiao.REGIAO_MARINGA)
                .segmento(Segmento.MEDICINA_SAUDE)
                .inativo(true)
                .descricaoMaterial("Descrição entity")
                .build();
    }

    @Test
    void deveTransformarParaDomain() {
        Cliente clienteTeste = ClienteMapper.paraDomain(clienteEntity);

        Assertions.assertEquals(clienteTeste.getId(), clienteEntity.getId());
        Assertions.assertEquals(clienteTeste.getNome(), clienteEntity.getNome());
        Assertions.assertEquals(clienteTeste.getTelefone(), clienteEntity.getTelefone());
        Assertions.assertEquals(clienteTeste.getRegiao(), clienteEntity.getRegiao());
        Assertions.assertEquals(clienteTeste.getSegmento(), clienteEntity.getSegmento());
        Assertions.assertTrue(clienteTeste.isInativo());
        Assertions.assertEquals(clienteTeste.getDescricaoMaterial(), clienteEntity.getDescricaoMaterial());
    }

    @Test
    void deveTransformarParaEntity() {
        ClienteEntity clienteTeste = ClienteMapper.paraEntity(clienteDomain);

        Assertions.assertEquals(clienteTeste.getId(), clienteDomain.getId());
        Assertions.assertEquals(clienteTeste.getNome(), clienteDomain.getNome());
        Assertions.assertEquals(clienteTeste.getTelefone(), clienteDomain.getTelefone());
        Assertions.assertEquals(clienteTeste.getRegiao(), clienteDomain.getRegiao());
        Assertions.assertEquals(clienteTeste.getSegmento(), clienteDomain.getSegmento());
        Assertions.assertFalse(clienteTeste.isInativo());
        Assertions.assertEquals(clienteTeste.getDescricaoMaterial(), clienteDomain.getDescricaoMaterial());
    }
}