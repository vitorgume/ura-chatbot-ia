package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.MidiaCliente;

import java.util.Optional;

public interface MidiaClienteGateway {
    Optional<MidiaCliente> consultarMidiaPeloTelefoneCliente(String telefone);
}
