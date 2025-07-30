package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.EscolhaNaoIdentificadoException;
import com.guminteligencia.ura_chatbot_ia.application.usecase.GatewayEnum;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.RelatorioContatoDto;
import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RelatorioMapperTest {

    private Object[] makeRow(String nome,
                             String telefone,
                             String segCode,
                             String regCode,
                             LocalDateTime dt,
                             String vendedornome) {
        Timestamp ts = Timestamp.valueOf(dt);
        return new Object[] { nome, telefone, segCode, regCode, ts, vendedornome };
    }

    @Test
    void deveTranformarParaDtoTudoValido() {
        var dt = LocalDateTime.of(2025, 7, 30, 14, 20);
        Object[] row = makeRow("Ana", "+5511999", "MED", "MAR", dt, "João");

        List<Object[]> input = Collections.singletonList(row);

        try (MockedStatic<GatewayEnum> ge = Mockito.mockStatic(GatewayEnum.class)) {
            ge.when(() -> GatewayEnum.gatewaySegmentoRelatorio("MED"))
                    .thenReturn(Segmento.MEDICINA_SAUDE);
            ge.when(() -> GatewayEnum.gatewayRegiaoRelatorio("MAR"))
                    .thenReturn(Regiao.MARINGA);

            List<RelatorioContatoDto> dtoList = RelatorioMapper.paraDto(input);

            assertEquals(1, dtoList.size());
            RelatorioContatoDto dto = dtoList.get(0);

            assertAll("campos principais",
                    () -> assertEquals("Ana", dto.getNome()),
                    () -> assertEquals("+5511999", dto.getTelefone()),
                    () -> assertEquals(dt, dto.getDataCriacao()),
                    () -> assertEquals("João", dto.getNomeVendedor())
            );
            assertEquals(Segmento.MEDICINA_SAUDE, dto.getSegmento());
            assertEquals(Regiao.MARINGA,          dto.getRegiao());
        }
    }


    @Test
    void deveTrasformarParaDtoSegmentoNaoValido() {
        var dt = LocalDateTime.of(2025, 7, 30, 14, 20);
        Object[] row = makeRow("Bruno", "+5522333", "BAD_SEG", "SP", dt, "Maria");

        List<Object[]> input = Collections.singletonList(row);

        try (MockedStatic<GatewayEnum> ge = Mockito.mockStatic(GatewayEnum.class)) {
            ge.when(() -> GatewayEnum.gatewaySegmentoRelatorio("BAD_SEG"))
                    .thenThrow(new EscolhaNaoIdentificadoException());
            ge.when(() -> GatewayEnum.gatewayRegiaoRelatorio("SP"))
                    .thenReturn(Regiao.OUTRA);

            RelatorioContatoDto dto = RelatorioMapper.paraDto(input).get(0);
            assertEquals(Segmento.NAO_INFORMADO, dto.getSegmento());
            assertEquals(Regiao.OUTRA,        dto.getRegiao());
        }
    }

    @Test
    void deveTrasformarParaDtoRegiaNaoValida() {
        var dt = LocalDateTime.of(2025, 7, 30, 14, 20);
        var row = makeRow("Carla", "+5533444", "TEC", "BAD_REG", dt, "Pedro");

        List<Object[]> input = Collections.singletonList(row);

        try (MockedStatic<GatewayEnum> ge = Mockito.mockStatic(GatewayEnum.class)) {
            ge.when(() -> GatewayEnum.gatewaySegmentoRelatorio("TEC"))
                    .thenReturn(Segmento.ENGENHARIA_ARQUITETURA);
            ge.when(() -> GatewayEnum.gatewayRegiaoRelatorio("BAD_REG"))
                    .thenThrow(new EscolhaNaoIdentificadoException());

            RelatorioContatoDto dto = RelatorioMapper.paraDto(input).get(0);
            assertEquals(Segmento.ENGENHARIA_ARQUITETURA,    dto.getSegmento());
            assertEquals(Regiao.NAO_INFORMADA,   dto.getRegiao());
        }
    }

    @Test
    void deveTrasformarParaDtoAmbosNaoValidos() {
        var dt = LocalDateTime.of(2025, 7, 30, 14, 20);
        var row = makeRow("Diego", "+5544555", "X", "Y", dt, "Ana");

        List<Object[]> input = Collections.singletonList(row);

        try (MockedStatic<GatewayEnum> ge = Mockito.mockStatic(GatewayEnum.class)) {
            ge.when(() -> GatewayEnum.gatewaySegmentoRelatorio("X"))
                    .thenThrow(EscolhaNaoIdentificadoException.class);
            ge.when(() -> GatewayEnum.gatewayRegiaoRelatorio("Y"))
                    .thenThrow(EscolhaNaoIdentificadoException.class);

            RelatorioContatoDto dto = RelatorioMapper.paraDto(input).get(0);
            assertEquals(Segmento.NAO_INFORMADO, dto.getSegmento());
            assertEquals(Regiao.NAO_INFORMADA,   dto.getRegiao());
        }
    }
}