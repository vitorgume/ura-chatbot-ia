package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import org.springframework.stereotype.Component;

@Component
public class EscolhaRegiaoMaringa implements EscolhaVendedorType {

    @Override
    public EscolhaVendedor escolher() {
        return EscolhaVendedor.builder()
                .roleta(false)
                .vendedor("Samara")
                .build();
    }

    @Override
    public boolean deveAplicar(Regiao regiao, Segmento segmento) {
        return regiao.getCodigo() == 2;
    }
}
