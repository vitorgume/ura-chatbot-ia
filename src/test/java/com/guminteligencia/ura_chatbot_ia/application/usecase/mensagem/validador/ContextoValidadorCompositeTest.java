package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.validador;

import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContextoValidadorCompositeTest {

    @Mock
    ContextoValidator v1, v2;

    Contexto contexto = mock(Contexto.class);

    @Test
    void permitirProcessarTrueSeQualquerValidadorRetornarTrue() {
        when(v1.permitirProcessar(contexto)).thenReturn(false);
        when(v2.permitirProcessar(contexto)).thenReturn(true);

        var composite = new ContextoValidadorComposite(List.of(v1, v2));
        assertTrue(composite.permitirProcessar(contexto));

        InOrder ord = inOrder(v1, v2);
        ord.verify(v1).permitirProcessar(contexto);
        ord.verify(v2).permitirProcessar(contexto);
        ord.verifyNoMoreInteractions();
    }

    @Test
    void deveIngnorarFalseSeNenhumValidadorRetornarTrue() {
        when(v1.permitirProcessar(contexto)).thenReturn(false);
        when(v2.permitirProcessar(contexto)).thenReturn(false);

        var composite = new ContextoValidadorComposite(List.of(v1, v2));
        assertFalse(composite.permitirProcessar(contexto));

        verify(v1).permitirProcessar(contexto);
        verify(v2).permitirProcessar(contexto);
        verifyNoMoreInteractions(v1, v2);
    }

}