package com.guminteligencia.ura_chatbot_ia.infrastructure.security.jwt;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private static final String SECRET = Base64.getEncoder()
            .encodeToString("minha-chave-secreta-1234567890".getBytes());

    JwtUtil util = new JwtUtil(SECRET);

    @Test
    void generateAndValidateToken() {
        String username = "usuarioTeste";
        String token = util.generateToken(username);

        assertNotNull(token);
        assertTrue(util.isTokenValid(token), "Token deve ser válido");
        assertEquals(username, util.extractUsername(token));
    }

    @Test
    void invalidTokenFailsValidation() {
        String badToken = "eyJhbGciOiJI...tokenInvalido";
        assertFalse(util.isTokenValid(badToken), "Token mal-formado não deve ser válido");
    }
}