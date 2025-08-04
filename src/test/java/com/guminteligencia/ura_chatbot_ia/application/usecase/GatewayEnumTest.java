package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.EscolhaNaoIdentificadoException;
import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GatewayEnumTest {

    @Test
    void gatewaySegmentoValoresValidos() {
        assertEquals(Segmento.MEDICINA_SAUDE, GatewayEnum.gatewaySegmento("1"));
        assertEquals(Segmento.BOUTIQUE_LOJAS, GatewayEnum.gatewaySegmento("2"));
        assertEquals(Segmento.ENGENHARIA_ARQUITETURA, GatewayEnum.gatewaySegmento("3"));
        assertEquals(Segmento.ALIMENTOS, GatewayEnum.gatewaySegmento("4"));
        assertEquals(Segmento.CELULARES, GatewayEnum.gatewaySegmento("5"));
        assertEquals(Segmento.OUTROS, GatewayEnum.gatewaySegmento("6"));
        assertEquals(Segmento.CELULARES, GatewayEnum.gatewaySegmento("5"));
    }

    @Test
    void gatewaySegmentoLancaExceptionParaValorInvalido() {
        assertThrows(EscolhaNaoIdentificadoException.class, () -> GatewayEnum.gatewaySegmento("0"));
        assertThrows(EscolhaNaoIdentificadoException.class, () -> GatewayEnum.gatewaySegmento("x"));
        assertThrows(EscolhaNaoIdentificadoException.class, () -> GatewayEnum.gatewaySegmento(""));
    }

    @Test
    void gatewayRegiaoValoresValidos() {
        assertEquals(Regiao.MARINGA, GatewayEnum.gatewayRegiao("1"));
        assertEquals(Regiao.REGIAO_MARINGA, GatewayEnum.gatewayRegiao("2"));
        assertEquals(Regiao.OUTRA, GatewayEnum.gatewayRegiao("3"));
    }

    @Test
    void gatewayRegiaoLancaExceptionParaValorInvalido() {
        assertThrows(EscolhaNaoIdentificadoException.class, () -> GatewayEnum.gatewayRegiao("0"));
        assertThrows(EscolhaNaoIdentificadoException.class, () -> GatewayEnum.gatewayRegiao("abc"));
    }

    @Test
    void gatewaySegmentoRelatorioValoresValidos() {
        assertEquals(Segmento.MEDICINA_SAUDE, GatewayEnum.gatewaySegmentoRelatorio("0"));
        assertEquals(Segmento.BOUTIQUE_LOJAS, GatewayEnum.gatewaySegmentoRelatorio("1"));
        assertEquals(Segmento.ENGENHARIA_ARQUITETURA, GatewayEnum.gatewaySegmentoRelatorio("2"));
        assertEquals(Segmento.ALIMENTOS, GatewayEnum.gatewaySegmentoRelatorio("3"));
        assertEquals(Segmento.CELULARES, GatewayEnum.gatewaySegmentoRelatorio("4"));
        assertEquals(Segmento.OUTROS, GatewayEnum.gatewaySegmentoRelatorio("5"));
    }

    @Test
    void gatewaySegmentoRelatorioLancaExceptionParaValorInvalido() {
        assertThrows(EscolhaNaoIdentificadoException.class, () -> GatewayEnum.gatewaySegmentoRelatorio("-1"));
        assertThrows(EscolhaNaoIdentificadoException.class, () -> GatewayEnum.gatewaySegmentoRelatorio("x"));
    }

    @Test
    void gatewayRegiaoRelatorioValoresValidos() {
        assertEquals(Regiao.MARINGA, GatewayEnum.gatewayRegiaoRelatorio("0"));
        assertEquals(Regiao.REGIAO_MARINGA, GatewayEnum.gatewayRegiaoRelatorio("1"));
        assertEquals(Regiao.OUTRA, GatewayEnum.gatewayRegiaoRelatorio("2"));
    }

    @Test
    void gatewayRegiaoRelatorioLancaExceptionParaValorInvalido() {
        assertThrows(EscolhaNaoIdentificadoException.class, () -> GatewayEnum.gatewayRegiaoRelatorio("3"));
        assertThrows(EscolhaNaoIdentificadoException.class, () -> GatewayEnum.gatewayRegiaoRelatorio("abc"));
    }
}