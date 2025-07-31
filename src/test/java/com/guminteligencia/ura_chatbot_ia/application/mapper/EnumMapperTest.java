package com.guminteligencia.ura_chatbot_ia.application.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnumMapperTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void deveDirecionarRegiaoCorreta() {
        Regiao regiaoMaringa = EnumMapper.regiaoMapper(1);
        Regiao regiaoRegiaoMaringa = EnumMapper.regiaoMapper(2);
        Regiao regiaoOutra = EnumMapper.regiaoMapper(3);
        Regiao regiaoNaoInformada = EnumMapper.regiaoMapper(10);

        Assertions.assertEquals(1, regiaoMaringa.getCodigo());
        Assertions.assertEquals(2, regiaoRegiaoMaringa.getCodigo());
        Assertions.assertEquals(3, regiaoOutra.getCodigo());
        Assertions.assertEquals(4, regiaoNaoInformada.getCodigo());
    }

    @Test
    void deveDirecionarSegmentoCorreto() {
        Segmento segmentoMedicina = EnumMapper.segmentoMapper(1);
        Segmento segmentoBoutique = EnumMapper.segmentoMapper(2);
        Segmento segmentoEngenharia = EnumMapper.segmentoMapper(3);
        Segmento segmentoAlimentos = EnumMapper.segmentoMapper(4);
        Segmento segmentoCelulares = EnumMapper.segmentoMapper(5);
        Segmento segmentoOutro = EnumMapper.segmentoMapper(6);
        Segmento segmentoNaoInformado = EnumMapper.segmentoMapper(10);

        Assertions.assertEquals(1, segmentoMedicina.getCodigo());
        Assertions.assertEquals(2, segmentoBoutique.getCodigo());
        Assertions.assertEquals(3, segmentoEngenharia.getCodigo());
        Assertions.assertEquals(4, segmentoAlimentos.getCodigo());
        Assertions.assertEquals(5, segmentoCelulares.getCodigo());
        Assertions.assertEquals(6, segmentoOutro.getCodigo());
        Assertions.assertEquals(7, segmentoNaoInformado.getCodigo());
    }
}