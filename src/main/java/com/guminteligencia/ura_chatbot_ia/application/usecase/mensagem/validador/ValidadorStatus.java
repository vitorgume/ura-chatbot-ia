package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.validador;

import com.guminteligencia.ura_chatbot_ia.application.usecase.MensageriaUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.ContextoUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidadorStatus implements ContextoValidator {

    private final ContextoUseCase contextoUseCase;
    private final MensageriaUseCase mensageriaUseCase;

    @Override
    public boolean deveIgnorar(Contexto contexto) {
        Contexto contextoSalvo = contextoUseCase.consultarPeloId(contexto.getId());
        boolean deveIgnorar = contextoSalvo.getStatus().getCodigo() == 1;

        if(deveIgnorar) {
            mensageriaUseCase.deletarMensagem(contexto.getMensagemFila());
        }

        return deveIgnorar;
    }
}
