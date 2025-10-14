package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.MidiaClienteNaoEncontradaException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.MidiaClienteGateway;
import com.guminteligencia.ura_chatbot_ia.domain.MidiaCliente;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MidiaClienteUseCase {

    private final MidiaClienteGateway gateway;

    public Optional<MidiaCliente> consultarMidiaPeloTelefoneCliente(String telefone) {
        return gateway.consultarMidiaPeloTelefoneCliente(telefone);
    }
}
