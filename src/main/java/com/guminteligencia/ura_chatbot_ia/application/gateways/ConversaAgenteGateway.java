package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversaAgenteGateway {
    ConversaAgente salvar(ConversaAgente conversaAgente);

    Optional<ConversaAgente> consultarPorIdCliente(UUID id);

    Optional<ConversaAgente> consultarPorId(UUID idConversa);

    List<ConversaAgente> listarNaoFinalizados();
}
