package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.EscolhaNaoIdentificadoException;
import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;

public class GatewayEnum {
    public static Segmento gatewaySegmento(String mensagem) {
        String mensagemFormatada = mensagem.toLowerCase();

        return switch (mensagemFormatada) {
            case "1" -> Segmento.MEDICINA_SAUDE;
            case "2" -> Segmento.BOUTIQUE_LOJAS;
            case "3" -> Segmento.ENGENHARIA_ARQUITETURA;
            case "4" -> Segmento.ALIMENTOS;
            case "5" -> Segmento.CELULARES;
            case "6" -> Segmento.OUTROS;
            default -> throw new EscolhaNaoIdentificadoException();
        };
    }

    public static Regiao gatewayRegiao(String mensagem) {
        String mensagemFormatada = mensagem.toLowerCase();

        return switch (mensagemFormatada) {
            case "1" -> Regiao.MARINGA;
            case "2" -> Regiao.REGIAO_MARINGA;
            case "3" -> Regiao.OUTRA;
            default -> throw new EscolhaNaoIdentificadoException();
        };
    }

    public static Segmento gatewaySegmentoRelatorio(String tipo) {
        String mensagemFormatada = tipo.toLowerCase();

        return switch (mensagemFormatada) {
            case "0" -> Segmento.MEDICINA_SAUDE;
            case "1" -> Segmento.BOUTIQUE_LOJAS;
            case "2" -> Segmento.ENGENHARIA_ARQUITETURA;
            case "3" -> Segmento.ALIMENTOS;
            case "4" -> Segmento.CELULARES;
            case "5" -> Segmento.OUTROS;
            default -> throw new EscolhaNaoIdentificadoException();
        };
    }

    public static Regiao gatewayRegiaoRelatorio(String tipo) {
        String mensagemFormatada = tipo.toLowerCase();

        return switch (mensagemFormatada) {
            case "0" -> Regiao.MARINGA;
            case "1" -> Regiao.REGIAO_MARINGA;
            case "2" -> Regiao.OUTRA;
            default -> throw new EscolhaNaoIdentificadoException();
        };
    }
}
