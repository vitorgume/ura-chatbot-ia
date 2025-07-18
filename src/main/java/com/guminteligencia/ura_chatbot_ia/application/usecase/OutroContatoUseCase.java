package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.OutroContatoNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.OutroContatoGateway;
import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OutroContatoUseCase {

    private final OutroContatoGateway gateway;

    public OutroContato consultarPorNome(String nome) {
        Optional<OutroContato> outroContato = gateway.consultarPorNome(nome);

        if(outroContato.isEmpty()) {
            throw new OutroContatoNaoEncontradoException();
        }

        return outroContato.get();
    }

}
