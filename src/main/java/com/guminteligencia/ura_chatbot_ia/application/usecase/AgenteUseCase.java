package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.gateways.AgenteGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.MensagemAgenteDto;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.RespostaAgente;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgenteUseCase {

    private final AgenteGateway gateway;

    public RespostaAgente enviarMensagem(Cliente clienteSalvo, ConversaAgente conversa, List<String> mensagens) {
        MensagemAgenteDto mensagem = MensagemAgenteDto.builder()
                .clienteId(clienteSalvo.getId().toString())
                .conversaId(conversa.getId().toString())
                .mensagem(this.concatenarMensagens(mensagens))
                .build();

        return gateway.enviarMensagem(mensagem);
    }

    private String concatenarMensagens(List<String> mensagens) {
        return mensagens.stream().collect(Collectors.joining(", "));
    }
}
