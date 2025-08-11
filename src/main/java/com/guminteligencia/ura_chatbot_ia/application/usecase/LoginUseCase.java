package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.CredenciasIncorretasException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.LoginGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Administrador;
import com.guminteligencia.ura_chatbot_ia.domain.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUseCase {
    
    private final AdministradorUseCase administradorUseCase;
    private final LoginGateway loginGateway;
    private final CriptografiaUseCase criptografiaUseCase;

    public LoginResponse autenticar(String telefone, String senha) {
        Administrador administrador = administradorUseCase.consultarPorTelefone(telefone);
        
        this.validaCredenciais(administrador, telefone, senha);
        
        String token = loginGateway.gerarToken(telefone);
        
        return LoginResponse.builder()
                .token(token)
                .id(administrador.getId())
                .build();
    }

    private void validaCredenciais(Administrador administrador, String email, String senha) {
        if(!administrador.getTelefone().equals(email) || !criptografiaUseCase.validaSenha(senha, administrador.getSenha())) {
            throw new CredenciasIncorretasException();
        }
    }

}
