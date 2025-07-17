package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.ClienteNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.ClienteGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

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

    public Cliente alterar(Cliente clienteQualificado, UUID idCliente) {
        Cliente cliente = this.consultarPorId(idCliente);
        cliente.setDados(clienteQualificado);
        return gateway.salvar(cliente);
    }

    private Cliente consultarPorId(UUID idCliente) {
        Optional<Cliente> cliente = gateway.consultarPorId(idCliente);

        if(cliente.isEmpty()) {
            throw new ClienteNaoEncontradoException();
        }

        return cliente.get();
    }


}
