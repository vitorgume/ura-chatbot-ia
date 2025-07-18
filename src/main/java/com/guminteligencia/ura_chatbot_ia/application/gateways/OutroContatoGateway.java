package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;

import java.util.Optional;

public interface OutroContatoGateway {
    Optional<OutroContato> consultarPorNome(String nome);
}
