package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.AdministradorJaExisteException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.AdministradorNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.AdministradorGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Administrador;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdministradorUseCase {

    private final AdministradorGateway gateway;
    private final CriptografiaUseCase criptografiaUseCase;

    public Administrador cadastrar(Administrador novoAdministrador) {
        log.info("Cadastrando novo administrador. Novo administrador: {}", novoAdministrador);

        Optional<Administrador> administrador = gateway.consultarPorEmail(novoAdministrador.getEmail());

        if(administrador.isPresent()) {
            throw new AdministradorJaExisteException();
        }

        novoAdministrador.setSenha(criptografiaUseCase.criptografar(novoAdministrador.getSenha()));

        Administrador administradorSalvo = gateway.salvar(novoAdministrador);

        log.info("Novo administrador cadastrado com sucesso. Administrador: {}", administradorSalvo);

        return administradorSalvo;
    }


    public void deletar(UUID id) {
        gateway.deletar(id);
    }

    public Administrador consultarPorEmail(String email) {
        Optional<Administrador> administrador = gateway.consultarPorEmail(email);

        if(administrador.isEmpty()) {
            throw new AdministradorNaoEncontradoException();
        }

        return administrador.get();
    }
}
