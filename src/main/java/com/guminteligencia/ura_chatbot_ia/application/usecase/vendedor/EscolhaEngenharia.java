package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import org.springframework.stereotype.Component;

@Component
public class EscolhaEngenharia implements EscolhaVendedorType {

    @Override
    public EscolhaVendedor escolher() {
        return EscolhaVendedor.builder()
                .roleta(true)
                .vendedor(null)
                .build();
    }

    @Override
    public boolean deveAplicar(Regiao regiao, Segmento segmento) {
        return segmento.getCodigo() == 3;
    }
}
