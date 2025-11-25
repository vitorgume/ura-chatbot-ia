package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.validador;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.ConversaAgenteNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ClienteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ConversaAgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.MensageriaUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class ValidadorTempoEspera implements ContextoValidator {

    private final ConversaAgenteUseCase conversaAgenteUseCase;
    private final ClienteUseCase clienteUseCase;

    @Override
    public boolean permitirProcessar(Contexto contexto) {
        return clienteUseCase.consultarPorTelefone(contexto.getTelefone())
                .map(cliente -> {
                    ConversaAgente conv;

                    try {
                         conv = conversaAgenteUseCase.consultarPorCliente(cliente.getId());
                    } catch (ConversaAgenteNaoEncontradoException exception) {
                        log.info("Conversa não existente para cliente com telefone: {}", cliente.getTelefone());
                        return true;
                    }

                    boolean aindaNoCooldown = conv.getFinalizada() &&
                            !conv.getDataUltimaMensagem().plusMinutes(30).isBefore(LocalDateTime.now());

                    if(aindaNoCooldown)
                        log.info("Ignorado pois ainda está no cooldown");

                    return !aindaNoCooldown;
                })
                .orElse(true);
    }
}
