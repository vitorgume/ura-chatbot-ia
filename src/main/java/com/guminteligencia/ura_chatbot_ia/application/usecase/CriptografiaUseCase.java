package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.gateways.CriptografiaGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CriptografiaUseCase {

    private final CriptografiaGateway gateway;

    public String criptografar(String senha) {
        return gateway.criptografar(senha);
    }

    public boolean validaSenha(String senha, String senhaAdmin) {
        return gateway.validarSenha(senha, senhaAdmin);
    }

}
