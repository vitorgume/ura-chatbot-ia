package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Prioridade;
import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.VendedorEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class VendedorMapperTest {

    private Vendedor vendedorDomain;
    private VendedorEntity vendedorEntity;

    @BeforeEach
    void setUp() {
        vendedorDomain = Vendedor.builder()
                .id(1L)
                .nome("Vendedor domain")
                .telefone("0000000000000")
                .inativo(false)
                .segmentos(List.of(Segmento.ALIMENTOS, Segmento.OUTROS))
                .regioes(List.of(Regiao.MARINGA))
                .prioridade(new Prioridade(1, true))
                .build();

        vendedorEntity = VendedorEntity.builder()
                .id(2L)
                .nome("Vendedor entity")
                .telefone("0000000000001")
                .inativo(true)
                .segmentos(List.of(Segmento.ENGENHARIA_ARQUITETURA, Segmento.BOUTIQUE_LOJAS))
                .regioes(List.of(Regiao.MARINGA, Regiao.OUTRA))
                .prioridade(new Prioridade(0, false))
                .build();
    }

    @Test
    void deveTransformarParaDomain() {
        Vendedor vendedorTeste = VendedorMapper.paraDomain(vendedorEntity);

        Assertions.assertEquals(vendedorTeste.getId(), vendedorEntity.getId());
        Assertions.assertEquals(vendedorTeste.getNome(), vendedorEntity.getNome());
        Assertions.assertEquals(vendedorTeste.getTelefone(), vendedorEntity.getTelefone());
        Assertions.assertTrue(vendedorTeste.getInativo());
        Assertions.assertEquals(vendedorTeste.getSegmentos(), vendedorEntity.getSegmentos());
        Assertions.assertEquals(vendedorTeste.getRegioes(), vendedorEntity.getRegioes());
        Assertions.assertEquals(vendedorTeste.getPrioridade(), vendedorEntity.getPrioridade());
    }

    @Test
    void deveTransformarParaEntity() {
        VendedorEntity vendedorTeste = VendedorMapper.paraEntity(vendedorDomain);

        Assertions.assertEquals(vendedorTeste.getId(), vendedorDomain.getId());
        Assertions.assertEquals(vendedorTeste.getNome(), vendedorDomain.getNome());
        Assertions.assertEquals(vendedorTeste.getTelefone(), vendedorDomain.getTelefone());
        Assertions.assertFalse(vendedorTeste.getInativo());
        Assertions.assertEquals(vendedorTeste.getSegmentos(), vendedorDomain.getSegmentos());
        Assertions.assertEquals(vendedorTeste.getRegioes(), vendedorDomain.getRegioes());
        Assertions.assertEquals(vendedorTeste.getPrioridade(), vendedorDomain.getPrioridade());
    }
}