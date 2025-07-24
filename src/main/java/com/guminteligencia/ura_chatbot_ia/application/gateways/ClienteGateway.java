package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.RelatorioContatoDto;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteGateway {
    Optional<Cliente> consultarPorTelefone(String telefone);

    Cliente salvar(Cliente cliente);

    Optional<Cliente> consultarPorId(UUID idCliente);

    List<RelatorioContatoDto> getRelatorioContato();

    List<RelatorioContatoDto> getRelatorioContatoSegundaFeira();
}
