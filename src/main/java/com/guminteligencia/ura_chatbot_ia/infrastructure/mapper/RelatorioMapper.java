package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.EscolhaNaoIdentificadoException;
import com.guminteligencia.ura_chatbot_ia.application.usecase.GatewayEnum;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.RelatorioContatoDto;
import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

public class RelatorioMapper {
    public static List<RelatorioContatoDto> paraDto(List<Object[]> objects) {
        return objects.stream()
                .map(obj -> {
                            RelatorioContatoDto relatorio = RelatorioContatoDto.builder()
                                    .nome((String) obj[0])
                                    .telefone((String) obj[1])
                                    .dataCriacao(((Timestamp) obj[4]).toLocalDateTime())
                                    .nomeVendedor((String) obj[5])
                                    .build();

                            try {
                                relatorio.setSegmento(GatewayEnum.gatewaySegmentoRelatorio(String.valueOf(obj[2])));
                            } catch (EscolhaNaoIdentificadoException ex) {
                                relatorio.setSegmento(Segmento.NAO_INFORMADO);
                            }

                            try {
                                relatorio.setRegiao(GatewayEnum.gatewayRegiaoRelatorio(String.valueOf(obj[3])));
                            } catch (EscolhaNaoIdentificadoException ex) {
                                relatorio.setRegiao(Regiao.NAO_INFORMADA);
                            }


                            return relatorio;
                        }
                )
                .collect(Collectors.toList());
    }
}
