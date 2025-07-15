package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;

public interface EscolhaVendedorType {
    EscolhaVendedor escolher();
    boolean deveAplicar(Regiao regiao, Segmento segmento);
}
