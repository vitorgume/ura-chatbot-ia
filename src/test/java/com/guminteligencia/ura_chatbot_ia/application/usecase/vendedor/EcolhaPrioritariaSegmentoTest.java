package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Prioridade;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EcolhaPrioritariaSegmentoTest {

    private final EcolhaPrioritariaSegmento ecolhaPrioritariaSegmento = new EcolhaPrioritariaSegmento();

    @Test
    void deveRetornaOptionalVazioSeNenhumEstaNoSegmentoOuPrioritario() {
        Cliente cli = Cliente.builder().segmento(Segmento.MEDICINA_SAUDE).build();

        Vendedor v1 = Vendedor.builder()
                .segmentos(List.of(Segmento.BOUTIQUE_LOJAS))
                .prioridade(new Prioridade(1, true))
                .build();
        Vendedor v2 = Vendedor.builder()
                .segmentos(List.of(Segmento.MEDICINA_SAUDE))
                .prioridade(new Prioridade(1, false))
                .build();

        Optional<Vendedor> res = ecolhaPrioritariaSegmento.escolher(cli, List.of(v1, v2));
        assertTrue(res.isEmpty());
    }

    @Test
    void deveEscolherMenorValorPrioritarioEdoSegmento() {
        Cliente cli = Cliente.builder().segmento(Segmento.MEDICINA_SAUDE).build();

        Vendedor alto = Vendedor.builder()
                .segmentos(List.of(Segmento.MEDICINA_SAUDE))
                .prioridade(new Prioridade(10, true))
                .build();
        Vendedor baixo = Vendedor.builder()
                .segmentos(List.of(Segmento.MEDICINA_SAUDE))
                .prioridade(new Prioridade(1, true))
                .build();

        Optional<Vendedor> res = ecolhaPrioritariaSegmento.escolher(cli, List.of(alto, baixo));
        assertTrue(res.isPresent());
        assertEquals(baixo, res.get());
    }
}