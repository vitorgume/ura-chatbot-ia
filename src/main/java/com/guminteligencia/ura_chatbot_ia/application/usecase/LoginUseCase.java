package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.CredenciasIncorretasException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.LoginGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Administrador;
import com.guminteligencia.ura_chatbot_ia.domain.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginUseCase {
    
    private final AdministradorUseCase administradorUseCase;
    private final LoginGateway loginGateway;
    private final CriptografiaUseCase criptografiaUseCase;

    public LoginResponse autenticar(String email, String senha) {
        Administrador administrador = administradorUseCase.consultarPorEmail(email);
        
        this.validaCredenciais(administrador, email, senha);
        
        String token = loginGateway.gerarToken(email);
        
        return LoginResponse.builder()
                .token(token)
                .id(administrador.getId())
                .build();
    }

    private void validaCredenciais(Administrador administrador, String email, String senha) {
        if(!administrador.getEmail().equals(email) || !criptografiaUseCase.validaSenha(senha, administrador.getSenha())) {
            throw new CredenciasIncorretasException();
        }
    }

}
