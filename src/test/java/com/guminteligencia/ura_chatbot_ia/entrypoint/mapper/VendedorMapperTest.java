package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Prioridade;
import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.VendedorDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class VendedorMapperTest {

    private Vendedor vendedorDomain;
    private VendedorDto vendedorDto;

    @BeforeEach
    void setUp() {
        vendedorDomain = Vendedor.builder()
                .id(1L)
                .nome("Nome teste")
                .telefone("554432165498778")
                .inativo(false)
                .segmentos(List.of(Segmento.ENGENHARIA_ARQUITETURA, Segmento.OUTROS))
                .regioes(List.of(Regiao.MARINGA, Regiao.REGIAO_MARINGA))
                .prioridade(new Prioridade(1, true))
                .build();

        vendedorDto = VendedorDto.builder()
                .id(2L)
                .nome("Nome teste 2")
                .telefone("554432165498779")
                .inativo(true)
                .segmentos(List.of(Segmento.BOUTIQUE_LOJAS, Segmento.MEDICINA_SAUDE))
                .regioes(List.of(Regiao.MARINGA))
                .prioridade(new Prioridade(1, false))
                .build();
    }

    @Test
    void deveRetornarDomain() {
        Vendedor vendedorTeste = VendedorMapper.paraDomain(vendedorDto);

        Assertions.assertEquals(vendedorDto.getId(), vendedorTeste.getId());
        Assertions.assertEquals(vendedorDto.getNome(), vendedorTeste.getNome());
        Assertions.assertEquals(vendedorDto.getTelefone(), vendedorTeste.getTelefone());
        Assertions.assertTrue(vendedorTeste.getInativo());
        Assertions.assertEquals(vendedorDto.getSegmentos(), vendedorTeste.getSegmentos());
        Assertions.assertEquals(vendedorDto.getRegioes(), vendedorTeste.getRegioes());
        Assertions.assertEquals(vendedorDto.getPrioridade(), vendedorTeste.getPrioridade());
    }

    @Test
    void deveRetornarDto() {
        VendedorDto vendedorTeste = VendedorMapper.paraDto(vendedorDomain);

        Assertions.assertEquals(vendedorDomain.getId(), vendedorTeste.getId());
        Assertions.assertEquals(vendedorDomain.getNome(), vendedorTeste.getNome());
        Assertions.assertEquals(vendedorDomain.getTelefone(), vendedorTeste.getTelefone());
        Assertions.assertFalse(vendedorTeste.getInativo());
        Assertions.assertEquals(vendedorDomain.getSegmentos(), vendedorTeste.getSegmentos());
        Assertions.assertEquals(vendedorDomain.getRegioes(), vendedorTeste.getRegioes());
        Assertions.assertEquals(vendedorDomain.getPrioridade(), vendedorTeste.getPrioridade());
    }
}