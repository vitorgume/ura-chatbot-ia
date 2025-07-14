package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.gateways.ClienteGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteUseCase {

    private final ClienteGateway gateway;

    public Optional<Cliente> consultarPorTelefone(String telefone) {
        return gateway.consultarPorTelefone(telefone);
    }

    public Cliente cadastrar(String telefone) {

        Cliente cliente = Cliente.builder()
                .telefone(telefone)
                .build();

        return gateway.salvar(cliente);
    }
}
