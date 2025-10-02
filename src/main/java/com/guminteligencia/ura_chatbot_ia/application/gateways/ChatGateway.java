package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.Chat;

import java.util.Optional;
import java.util.UUID;

public interface ChatGateway {
    Optional<Chat> consultarPorId(UUID idChat);

    Chat salvar(Chat chat);
}
