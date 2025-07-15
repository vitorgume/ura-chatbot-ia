package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.EscolhaNaoIdentificadoException;
import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EscolhaVendedorFactory {

    private final List<EscolhaVendedorType> escolhas;

    public EscolhaVendedorType escolha(Segmento segmento, Regiao regiao) {
        return escolhas.stream()
                .filter(escolha -> escolha.deveAplicar(regiao, segmento))
                .findFirst()
                .orElseThrow(EscolhaNaoIdentificadoException::new);
    }

}
