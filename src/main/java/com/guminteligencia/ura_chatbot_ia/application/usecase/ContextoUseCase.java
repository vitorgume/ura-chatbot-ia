package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.ContextoNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.ContextoGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContextoUseCase {

    private final ContextoGateway gateway;

    public void deletar(UUID id) {
        log.info("Deletando contexto. Id: {}", id);
        Contexto contexto = this.consultarPeloId(id);
        gateway.deletar(contexto);
        log.info("Contexto deletado com sucesso.");
    }

    public Contexto consultarPeloId(UUID id) {
        Optional<Contexto> contexto = gateway.consultarPorId(id);

        if(contexto.isEmpty()) {
            throw new ContextoNaoEncontradoException();
        }

        return contexto.get();
    }
}
