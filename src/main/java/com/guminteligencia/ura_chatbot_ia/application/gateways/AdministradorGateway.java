package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.Administrador;

import java.util.Optional;
import java.util.UUID;

public interface AdministradorGateway {
    Optional<Administrador> consultarPorEmail(String email);

    Administrador salvar(Administrador novoAdministrador);

    void deletar(UUID id);
}
