package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Prioridade;
import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EscolhaPrioritariaRegiaoTest {

    private final EscolhaPrioritariaRegiao escolhaPrioritariaRegiao = new EscolhaPrioritariaRegiao();

    @Test
    void deveRetornarOptionalVazioSeNenhumEstaNaRegiaoOuPrioritario() {
        Cliente cli = Cliente.builder().regiao(Regiao.MARINGA).build();
        Vendedor v1 = Vendedor.builder()
                .regioes(List.of(Regiao.OUTRA))
                .prioridade(new Prioridade(1, true))
                .build();
        Vendedor v2 = Vendedor.builder()
                .regioes(List.of(Regiao.MARINGA))
                .prioridade(new Prioridade(1, false))
                .build();

        Optional<Vendedor> res = escolhaPrioritariaRegiao.escolher(cli, List.of(v1, v2));
        assertTrue(res.isEmpty());
    }

    @Test
    void deveEscolherMenorValorPrioritarioEdaRegiao() {
        Cliente cli = Cliente.builder().regiao(Regiao.MARINGA).build();
        Vendedor alto = Vendedor.builder()
                .regioes(List.of(Regiao.MARINGA))
                .prioridade(new Prioridade(20, true))
                .build();
        Vendedor baixo = Vendedor.builder()
                .regioes(List.of(Regiao.MARINGA))
                .prioridade(new Prioridade(2, true))
                .build();

        Optional<Vendedor> res = escolhaPrioritariaRegiao.escolher(cli, List.of(alto, baixo));
        assertTrue(res.isPresent());
        assertEquals(baixo, res.get());
    }
}