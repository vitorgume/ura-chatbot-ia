package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.infrastructure.security.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginDataProviderTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private LoginDataProvider provider;

    private final String email = "user@example.com";
    private final String token = "jwt-token-xyz";

    @Test
    void deveDelegarGeracaoDeTokenParaJwtUtil() {
        when(jwtUtil.generateToken(email)).thenReturn(token);

        String result = provider.gerarToken(email);

        assertEquals(token, result);
        verify(jwtUtil, times(1)).generateToken(email);
    }

    @Test
    void deveLancarExceptionAoGerarToken() {
        when(jwtUtil.generateToken(anyString()))
                .thenThrow(new RuntimeException("jwt-fail"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> provider.gerarToken(email)
        );
        assertEquals("jwt-fail", ex.getMessage());
        verify(jwtUtil, times(1)).generateToken(email);
    }

}