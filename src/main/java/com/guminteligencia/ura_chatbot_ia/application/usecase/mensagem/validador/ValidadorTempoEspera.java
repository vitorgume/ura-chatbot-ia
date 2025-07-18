package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.validador;

import com.guminteligencia.ura_chatbot_ia.application.usecase.ClienteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ConversaAgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ValidadorTempoEspera implements ContextoValidator {

    private final ConversaAgenteUseCase conversaAgenteUseCase;
    private final ClienteUseCase clienteUseCase;

    @Override
    public boolean deveIgnorar(Contexto contexto) {
        Optional<Cliente> cliente = clienteUseCase.consultarPorTelefone(contexto.getTelefone());

        if(cliente.isPresent()) {
            ConversaAgente conversaAgente = conversaAgenteUseCase.consultarPorCliente(cliente.get().getId());
            return conversaAgente.getFinalizada() && conversaAgente.getDataUltimaMensagem().isBefore(LocalDateTime.now());
        }

        return false;
    }
}
