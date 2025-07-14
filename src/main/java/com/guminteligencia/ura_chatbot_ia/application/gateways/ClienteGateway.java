package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;

import java.util.Optional;

public interface ClienteGateway {
    Optional<Cliente> consultarPorTelefone(String telefone);

    Cliente salvar(Cliente cliente);
}
