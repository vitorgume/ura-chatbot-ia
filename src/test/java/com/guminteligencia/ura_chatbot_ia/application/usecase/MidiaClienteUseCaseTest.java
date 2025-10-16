package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.gateways.MidiaClienteGateway;
import com.guminteligencia.ura_chatbot_ia.domain.MidiaCliente;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MidiaClienteUseCaseTest {

    @Mock
    private MidiaClienteGateway gateway;

    @InjectMocks
    private MidiaClienteUseCase useCase;

    private final String telefone = "+5511999999999";

    @Test
    void deveConsultarMidiaPorTelefoneComSucesso() {
        MidiaCliente midia = mock(MidiaCliente.class);
        when(gateway.consultarMidiaPeloTelefoneCliente(telefone))
                .thenReturn(Optional.of(midia));

        Optional<MidiaCliente> out = useCase.consultarMidiaPeloTelefoneCliente(telefone);

        assertTrue(out.isPresent());
        assertSame(midia, out.get());
        verify(gateway, times(1)).consultarMidiaPeloTelefoneCliente(telefone);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void deveRetornarEmptyQuandoGatewayNaoEncontrar() {
        when(gateway.consultarMidiaPeloTelefoneCliente(telefone))
                .thenReturn(Optional.empty());

        Optional<MidiaCliente> out = useCase.consultarMidiaPeloTelefoneCliente(telefone);

        assertTrue(out.isEmpty());
        verify(gateway, times(1)).consultarMidiaPeloTelefoneCliente(telefone);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void devePropagarExcecaoDoGateway() {
        when(gateway.consultarMidiaPeloTelefoneCliente(telefone))
                .thenThrow(new DataProviderException("falha", null));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> useCase.consultarMidiaPeloTelefoneCliente(telefone)
        );

        assertEquals("falha", ex.getMessage());
        verify(gateway, times(1)).consultarMidiaPeloTelefoneCliente(telefone);
        verifyNoMoreInteractions(gateway);
    }

}