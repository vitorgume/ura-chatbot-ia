package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import ch.qos.logback.core.net.server.Client;
import com.guminteligencia.ura_chatbot_ia.domain.Canal;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ClienteDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClienteMapperTest {

    private Cliente clienteDomain;

    @BeforeEach
    void setUp() {
        clienteDomain = Cliente.builder()
                .id(UUID.randomUUID())
                .nome("Nome teste")
                .telefone("5544998748377")
                .regiao(Regiao.REGIAO_MARINGA)
                .segmento(Segmento.BOUTIQUE_LOJAS)
                .inativo(true)
                .descricaoMaterial("Descricao material teste")
                .canal(Canal.URA)
                .build();
    }

    @Test
    void deveRetornarDtoComSucesso() {
        ClienteDto resultado = ClienteMapper.paraDto(clienteDomain);

        Assertions.assertEquals(clienteDomain.getId(), resultado.getId());
        Assertions.assertEquals(clienteDomain.getNome(), resultado.getNome());
        Assertions.assertEquals(clienteDomain.getTelefone(), resultado.getTelefone());
        Assertions.assertEquals(clienteDomain.getRegiao().getCodigo(), resultado.getRegiao().getCodigo());
        Assertions.assertEquals(clienteDomain.getSegmento().getCodigo(), resultado.getSegmento().getCodigo());
        Assertions.assertTrue(clienteDomain.isInativo());
        Assertions.assertEquals(clienteDomain.getDescricaoMaterial(), resultado.getDescricaoMaterial());
        Assertions.assertEquals(clienteDomain.getCanal().getCodigo(), resultado.getCanal().getCodigo());
    }
}