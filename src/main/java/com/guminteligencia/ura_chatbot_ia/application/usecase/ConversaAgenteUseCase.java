package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.ConversaAgenteNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.ConversaAgenteGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversaAgenteUseCase {

    private final ConversaAgenteGateway gateway;

    public ConversaAgente criar(Cliente cliente) {
        log.info("Criando conversa do agente. Cliente: {}", cliente);

        ConversaAgente conversaAgente = ConversaAgente.builder()
                .cliente(cliente)
                .dataCriacao(LocalDateTime.now())
                .finalizada(false)
                .inativa(false)
                .build();

        ConversaAgente conversa = gateway.salvar(conversaAgente);

        log.info("Conversa do agente criada com sucesso. Conversa: {}", conversa);

        return conversa;
    }

    public ConversaAgente consultarPorCliente(UUID id) {
        log.info("Consultando conversa pelo cliente. Id cliente: {}", id);

        Optional<ConversaAgente> conversaAgente = gateway.consultarPorIdCliente(id);

        if(conversaAgente.isEmpty()) throw new ConversaAgenteNaoEncontradoException();

        ConversaAgente conversa = conversaAgente.get();

        log.info("Conversa consultada pelo cliente com sucesso. Conversa: {}", conversa);

        return conversa;
    }

    public void salvar(ConversaAgente conversaAgente) {
        gateway.salvar(conversaAgente);
    }
}
