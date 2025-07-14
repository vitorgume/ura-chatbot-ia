package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;

import java.util.Optional;
import java.util.UUID;

public interface ConversaAgenteGateway {
    ConversaAgente salvar(ConversaAgente conversaAgente);

    Optional<ConversaAgente> consultarPorId(UUID id);
}
