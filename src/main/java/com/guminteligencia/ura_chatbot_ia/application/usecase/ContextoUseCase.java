package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.gateways.ContextoGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContextoUseCase {

    private final ContextoGateway gateway;

    public void deletar(UUID id) {
        gateway.deletar(id);
    }
}
