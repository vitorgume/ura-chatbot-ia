package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.validador;

import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContextoValidadorComposite {

    private final List<ContextoValidator> validators;

    public boolean deveIgnorar(Contexto contexto) {
        return validators.stream().anyMatch(validator -> validator.deveIgnorar(contexto));
    }
}
