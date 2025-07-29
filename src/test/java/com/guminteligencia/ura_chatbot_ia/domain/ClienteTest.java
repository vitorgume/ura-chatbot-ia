package com.guminteligencia.ura_chatbot_ia.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(UUID.randomUUID())
                .nome("Nome teste")
                .telefone("000000000000")
                .regiao(Regiao.REGIAO_MARINGA)
                .segmento(Segmento.MEDICINA_SAUDE)
                .inativo(false)
                .descricaoMaterial("Descrição teste")
                .build();
    }

    @Test
    void deveAlterarTodosOsDados() {
        Cliente novosDados = Cliente.builder()
                .id(UUID.randomUUID())
                .nome("Nome teste 2")
                .telefone("00000000001")
                .regiao(Regiao.MARINGA)
                .segmento(Segmento.ALIMENTOS)
                .inativo(true)
                .descricaoMaterial("Descrição teste 2")
                .build();

        cliente.setDados(novosDados);

        Assertions.assertNotEquals(cliente.getId(), novosDados.getId());
        Assertions.assertEquals(cliente.getNome(), novosDados.getNome());
        Assertions.assertNotEquals(cliente.getTelefone(), novosDados.getTelefone());
        Assertions.assertEquals(cliente.getRegiao(), novosDados.getRegiao());
        Assertions.assertEquals(cliente.getSegmento(), novosDados.getSegmento());
        Assertions.assertFalse(cliente.isInativo());
        Assertions.assertNotEquals(cliente.getDescricaoMaterial(), novosDados.getDescricaoMaterial());
    }
}