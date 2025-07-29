package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.LoginGateway;
import com.guminteligencia.ura_chatbot_ia.infrastructure.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginDataProvider implements LoginGateway {

    private final JwtUtil jwtUtil;

    @Override
    public String gerarToken(String email) {
        return jwtUtil.generateToken(email);
    }
}
