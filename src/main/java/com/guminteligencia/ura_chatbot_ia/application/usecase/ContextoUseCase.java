package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.ContextoNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.ContextoGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContextoUseCase {

    private final ContextoGateway gateway;

    public void deletar(UUID id) {
        gateway.deletar(id);
    }

    public Contexto consultarPeloId(UUID id) {
        Optional<Contexto> contexto = gateway.consultarPorId(id);

        if(contexto.isEmpty()) {
            throw new ContextoNaoEncontradoException();
        }

        return contexto.get();
    }
}
