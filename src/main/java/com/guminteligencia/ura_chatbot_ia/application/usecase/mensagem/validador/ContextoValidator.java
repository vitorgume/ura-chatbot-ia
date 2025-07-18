package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.validador;

import com.guminteligencia.ura_chatbot_ia.domain.Contexto;

public interface ContextoValidator {
    boolean deveIgnorar(Contexto contexto);
}
