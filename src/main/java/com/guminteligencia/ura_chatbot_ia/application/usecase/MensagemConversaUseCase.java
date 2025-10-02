package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.gateways.MensagemConversaGateway;
import com.guminteligencia.ura_chatbot_ia.domain.MensagemConversa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MensagemConversaUseCase {

    private final MensagemConversaGateway gateway;

    public List<MensagemConversa> listarPelaConversa(UUID idConversa) {
        return gateway.listarPelaConversa(idConversa);
    }
}
