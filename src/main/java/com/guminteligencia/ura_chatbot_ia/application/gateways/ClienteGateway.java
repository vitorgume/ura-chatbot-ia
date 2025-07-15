package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;

import java.util.Optional;
import java.util.UUID;

public interface ClienteGateway {
    Optional<Cliente> consultarPorTelefone(String telefone);

    Cliente salvar(Cliente cliente);

    Optional<Cliente> consultarPorId(UUID idCliente);
}
