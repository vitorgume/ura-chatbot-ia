package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.validador;

import com.guminteligencia.ura_chatbot_ia.application.usecase.ClienteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ConversaAgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.MensageriaUseCase;
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
    private final MensageriaUseCase mensageriaUseCase;

    @Override
    public boolean deveIgnorar(Contexto contexto) {
        Optional<Cliente> cliente = clienteUseCase.consultarPorTelefone(contexto.getTelefone());

        if(cliente.isPresent()) {
            ConversaAgente conversaAgente = conversaAgenteUseCase.consultarPorCliente(cliente.get().getId());
            boolean deveIgnorar = conversaAgente.getFinalizada() && conversaAgente.getDataUltimaMensagem().isBefore(LocalDateTime.now());

            if(deveIgnorar) {
                mensageriaUseCase.deletarMensagem(contexto.getMensagemFila());
            }

            return deveIgnorar;
        }

        return false;
    }
}
