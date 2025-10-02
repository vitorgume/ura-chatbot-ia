package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.MensagemConversa;

import java.util.List;
import java.util.UUID;

public interface MensagemConversaGateway {
    List<MensagemConversa> listarPelaConversa(UUID idConversa);
}
