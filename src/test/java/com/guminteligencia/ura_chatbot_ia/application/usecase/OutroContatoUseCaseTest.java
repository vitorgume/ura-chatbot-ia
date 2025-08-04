package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.OutroContatoNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.OutroContatoGateway;
import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OutroContatoUseCaseTest {

    @Mock
    private OutroContatoGateway gateway;

    @InjectMocks
    private OutroContatoUseCase outroContatoUseCase;

    private OutroContato outroContatoTeste;

    @BeforeEach
    void setUp() {
        outroContatoTeste = OutroContato.builder().id(1L).nome("Nome teste").build();
    }

    @Test
    void deveConsultarPorNomeComSucesso() {
        Mockito.when(gateway.consultarPorNome(Mockito.anyString())).thenReturn(Optional.of(outroContatoTeste));

        OutroContato resultadoTeste = outroContatoUseCase.consultarPorNome(outroContatoTeste.getNome());


        Assertions.assertEquals(outroContatoTeste.getId(), resultadoTeste.getId());
        Mockito.verify(gateway, Mockito.times(1)).consultarPorNome(Mockito.anyString());
    }

    @Test
    void deveLancarExceptionQuandoConsultarRetornarVazia() {
        Mockito.when(gateway.consultarPorNome(Mockito.anyString())).thenReturn(Optional.empty());

        OutroContatoNaoEncontradoException exception = Assertions
                .assertThrows(OutroContatoNaoEncontradoException.class,
                        () -> outroContatoUseCase.consultarPorNome(outroContatoTeste.getNome())
                );

        Assertions.assertEquals("Outro contato não encontrado.", exception.getMessage());
    }
}