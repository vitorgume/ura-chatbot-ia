package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.gateways.CriptografiaGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriptografiaUseCaseTest {

    @Mock
    private CriptografiaGateway gateway;

    @InjectMocks
    private CriptografiaUseCase useCase;

    private final String senha = "minhaSenha";
    private final String hash = "HASHED";

    @Test
    void criptografarDeveDelegarParaGateway() {
        when(gateway.criptografar(senha)).thenReturn(hash);

        String resultado = useCase.criptografar(senha);

        assertEquals(hash, resultado, "Deve retornar o hash fornecido pelo gateway");
        verify(gateway).criptografar(senha);
    }

    @Test
    void validaSenhaDeveRetornarTrueQuandoGatewayValidarCorretamente() {
        when(gateway.validarSenha(senha, hash)).thenReturn(true);

        boolean valido = useCase.validaSenha(senha, hash);

        assertTrue(valido, "Deve retornar true quando a senha for válida");
        verify(gateway).validarSenha(senha, hash);
    }

    @Test
    void validaSenhaDeveRetornarFalseQuandoGatewayInvalidar() {
        when(gateway.validarSenha(senha, hash)).thenReturn(false);

        boolean valido = useCase.validaSenha(senha, hash);

        assertFalse(valido, "Deve retornar false quando a senha for inválida");
        verify(gateway).validarSenha(senha, hash);
    }
}