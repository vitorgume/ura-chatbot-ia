package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.MidiaClienteNaoEncontradaException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.MidiaClienteGateway;
import com.guminteligencia.ura_chatbot_ia.domain.MidiaCliente;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MidiaClienteUseCase {

    private final MidiaClienteGateway gateway;

    public Optional<MidiaCliente> consultarMidiaPeloTelefoneCliente(String telefone) {
        log.info("Consultando midia pelo telefone do cliente. Telefone: {}", telefone);
        Optional<MidiaCliente> midiaCliente = gateway.consultarMidiaPeloTelefoneCliente(telefone);
        log.info("Midia consultada com sucesso. Midia: {}", midiaCliente);

        return midiaCliente;
    }

    public void deletarMidiasCliente(String telefone) {
        log.info("Deletando midias do cliente. Telefone: {}", telefone);
        gateway.deletarMidiasCliente(telefone);
        log.info("Deletado midias do cliente com sucesso.");
    }
}
