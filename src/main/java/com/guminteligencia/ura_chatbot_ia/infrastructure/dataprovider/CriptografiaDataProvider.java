package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.CriptografiaGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CriptografiaDataProvider implements CriptografiaGateway {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String criptografar(String senha) {
        return passwordEncoder.encode(senha);
    }

    @Override
    public boolean validarSenha(String senha, String senhaAdmin) {
        return passwordEncoder.matches(senha, senhaAdmin);
    }
}
