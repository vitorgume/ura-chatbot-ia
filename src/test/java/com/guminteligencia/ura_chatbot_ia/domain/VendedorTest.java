package com.guminteligencia.ura_chatbot_ia.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VendedorTest {

    private Vendedor vendedor;

    @BeforeEach
    void setUp() {
        vendedor = Vendedor.builder()
                .id(1L)
                .nome("Nome teste")
                .telefone("000000000000")
                .inativo(false)
                .segmentos(List.of(Segmento.ALIMENTOS, Segmento.OUTROS))
                .regioes(List.of(Regiao.REGIAO_MARINGA, Regiao.MARINGA))
                .prioridade(new Prioridade(1, true))
                .build();
    }

    @Test
    void deveAlterarDadosDeVendedor() {
        Vendedor novosDados = Vendedor.builder()
                .id(2L)
                .nome("Nome teste 2")
                .telefone("000000000001")
                .inativo(true)
                .segmentos(List.of(Segmento.MEDICINA_SAUDE, Segmento.ENGENHARIA_ARQUITETURA))
                .regioes(List.of(Regiao.MARINGA))
                .prioridade(new Prioridade(0, false))
                .build();

        vendedor.setDados(novosDados);

        Assertions.assertNotEquals(vendedor.getId(), novosDados.getId());
        Assertions.assertEquals(vendedor.getNome(), novosDados.getNome());
        Assertions.assertEquals(vendedor.getTelefone(), novosDados.getTelefone());
        Assertions.assertTrue(vendedor.getInativo());
        Assertions.assertEquals(vendedor.getSegmentos(), novosDados.getSegmentos());
        Assertions.assertEquals(vendedor.getRegioes(), novosDados.getRegioes());
        Assertions.assertEquals(vendedor.getPrioridade(), novosDados.getPrioridade());
    }
}