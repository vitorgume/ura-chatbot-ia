package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriptografiaDataProviderTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CriptografiaDataProvider provider;

    private final String rawPassword    = "minhaSenha123";
    private final String encodedPassword = "$2a$10$abcdef...";

    @Test
    void deveDelegarCriptografiaParaPasswordEnconder() {
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        String result = provider.criptografar(rawPassword);

        assertEquals(encodedPassword, result);
        verify(passwordEncoder, times(1)).encode(rawPassword);
    }

    @Test
    void deveValidarSenhaQuandoMatchesRetornaTrue() {
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        boolean ok = provider.validarSenha(rawPassword, encodedPassword);

        assertTrue(ok);
        verify(passwordEncoder, times(1)).matches(rawPassword, encodedPassword);
    }

    @Test
    void deveValidarSenhaQuandoMatchesRetornaFalse() {
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        boolean ok = provider.validarSenha(rawPassword, encodedPassword);

        assertFalse(ok);
        verify(passwordEncoder, times(1)).matches(rawPassword, encodedPassword);
    }

    @Test
    void deveLancarExceptionQuandoCriptografar() {
        when(passwordEncoder.encode(anyString()))
                .thenThrow(new RuntimeException("encode-error"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> provider.criptografar(rawPassword)
        );
        assertEquals("encode-error", ex.getMessage());
    }

    @Test
    void deveLancarExceptionAoValidarSenha() {
        when(passwordEncoder.matches(anyString(), anyString()))
                .thenThrow(new RuntimeException("matches-error"));

        // act & assert
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> provider.validarSenha(rawPassword, encodedPassword)
        );
        assertEquals("matches-error", ex.getMessage());
    }

}