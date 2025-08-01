package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.ContextoNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.ContextoGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContextoUseCaseTest {

    @Mock
    private ContextoGateway contextoGateway;

    @InjectMocks
    private ContextoUseCase contextoUseCase;

    private Contexto contexto;

    @BeforeEach
    void setUp() {
        contexto = Contexto.builder().id(UUID.randomUUID()).build();
    }

    @Test
    void deveDeletarComSucesso() {
        when(contextoGateway.consultarPorId(any())).thenReturn(Optional.of(contexto));
        doNothing().when(contextoGateway).deletar(any());

        contextoUseCase.deletar(contexto.getId());

        Mockito.verify(contextoGateway, times(1)).deletar(any());
    }

    @Test
    void naoDeveDeletarQuandoConsultarPorIdLancarException() {
        when(contextoGateway.consultarPorId(any())).thenReturn(Optional.empty());

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> contextoUseCase.deletar(contexto.getId()));

        Assertions.assertEquals("Contexto não encontrado.", exception.getMessage());
        Mockito.verify(contextoGateway, never()).deletar(any());
    }

    @Test
    void deveConsultarPorIdComSucesso() {
        when(contextoGateway.consultarPorId(any())).thenReturn(Optional.of(contexto));

        Contexto contextoTeste = contextoUseCase.consultarPeloId(contexto.getId());

        Assertions.assertEquals(contextoTeste.getId(), contexto.getId());
    }

    @Test
    void deveLancarExceptionAoRetornarContextoNulo() {
        when(contextoGateway.consultarPorId(any())).thenReturn(Optional.empty());

        ContextoNaoEncontradoException exception = Assertions.assertThrows(ContextoNaoEncontradoException.class, () -> contextoUseCase.consultarPeloId(contexto.getId()));

        Assertions.assertEquals("Contexto não encontrado.", exception.getMessage());
    }
}