package com.guminteligencia.ura_chatbot_ia.application.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnumMapperTest {

    @Test
    void deveMapearRegiaoECairNoDefaultQuandoCodigoNaoPrevisto() {
        assertEquals(Regiao.MARINGA, EnumMapper.regiaoMapper(1));
        assertEquals(Regiao.REGIAO_MARINGA, EnumMapper.regiaoMapper(2));
        assertEquals(Regiao.OUTRA, EnumMapper.regiaoMapper(3));
        assertEquals(Regiao.NAO_INFORMADA, EnumMapper.regiaoMapper(99));
    }

    @Test
    void deveMapearSegmentoECairNoDefaultQuandoCodigoNaoPrevisto() {
        assertEquals(Segmento.MEDICINA_SAUDE, EnumMapper.segmentoMapper(1));
        assertEquals(Segmento.BOUTIQUE_LOJAS, EnumMapper.segmentoMapper(2));
        assertEquals(Segmento.ENGENHARIA_ARQUITETURA, EnumMapper.segmentoMapper(3));
        assertEquals(Segmento.ALIMENTOS, EnumMapper.segmentoMapper(4));
        assertEquals(Segmento.CELULARES, EnumMapper.segmentoMapper(5));
        assertEquals(Segmento.OUTROS, EnumMapper.segmentoMapper(6));
        assertEquals(Segmento.NAO_INFORMADO, EnumMapper.segmentoMapper(0));
    }
}
