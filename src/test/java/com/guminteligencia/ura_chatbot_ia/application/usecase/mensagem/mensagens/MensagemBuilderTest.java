package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MensagemBuilderTest {

    @Mock
    private MensagemFactory mensagemFactory;

    @Mock
    private MensagemType mensagemType;

    @InjectMocks
    private MensagemBuilder builder;

    private final TipoMensagem tipo = TipoMensagem.MENSAGEM_ALERTA_RECONTATO;
    private final String nomeVendedor = "VendedorX";
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder().nome("ClienteY").telefone("+5511999000111").build();
    }

    @Test
    void deveDelegarCorretamenteComSucesso() {
        String expected = "mensagem-formatada";
        when(mensagemFactory.create(tipo)).thenReturn(mensagemType);
        when(mensagemType.getMensagem(nomeVendedor, cliente)).thenReturn(expected);

        String result = builder.getMensagem(tipo, nomeVendedor, cliente);

        assertEquals(expected, result);

        verify(mensagemFactory, times(1)).create(tipo);
        verify(mensagemType, times(1)).getMensagem(nomeVendedor, cliente);
    }

    @Test
    void deveLancarExceptionQuandoRetornaNullFactory() {
        when(mensagemFactory.create(tipo)).thenReturn(null);

        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> builder.getMensagem(tipo, nomeVendedor, cliente)
        );

        assertNotNull(ex);
        verify(mensagemFactory).create(tipo);
    }
}