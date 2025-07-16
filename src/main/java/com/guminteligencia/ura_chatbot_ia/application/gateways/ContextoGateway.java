package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.Contexto;

import java.util.Optional;
import java.util.UUID;

public interface ContextoGateway {
    void deletar(Contexto contexto);

    Optional<Contexto> consultarPorId(UUID id);
}
