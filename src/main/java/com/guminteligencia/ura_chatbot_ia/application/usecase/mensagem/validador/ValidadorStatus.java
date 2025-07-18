package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.validador;

import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import org.springframework.stereotype.Component;

@Component
public class ValidadorStatus implements ContextoValidator {

    @Override
    public boolean deveIgnorar(Contexto contexto) {
        return contexto.getStatus().getCodigo() == 1;
    }
}
