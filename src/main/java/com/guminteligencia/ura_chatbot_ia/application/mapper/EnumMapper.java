package com.guminteligencia.ura_chatbot_ia.application.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;

public class EnumMapper {

    public static Regiao regiaoMapper(int codigo) {
        return switch (codigo) {
            case 1 -> Regiao.MARINGA;
            case 2 -> Regiao.REGIAO_MARINGA;
            case 3 -> Regiao.OUTRA;
            default -> Regiao.NAO_INFORMADA;
        };
    }

    public static Segmento segmentoMapper(int codigo) {
        return switch (codigo) {
            case 1 -> Segmento.MEDICINA_SAUDE;
            case 2 -> Segmento.BOUTIQUE_LOJAS;
            case 3 -> Segmento.ENGENHARIA_ARQUITETURA;
            case 4 -> Segmento.ALIMENTOS;
            case 5 -> Segmento.CELULARES;
            case 6 -> Segmento.OUTROS;
            default -> Segmento.NAO_INFORMADO;
        };
    }
}
