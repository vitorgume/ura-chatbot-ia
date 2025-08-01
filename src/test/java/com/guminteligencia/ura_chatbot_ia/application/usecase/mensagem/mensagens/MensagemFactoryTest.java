package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.EscolhaNaoIdentificadoException;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MensagemFactoryTest {

    @Test
    void deveRetornarMensagemDeAcordoComValidacao() {
        int desiredCode = TipoMensagem.MENSAGEM_ALERTA_RECONTATO.getCodigo();
        MensagemType matcher = mock(MensagemType.class);
        when(matcher.getTipoMensagem()).thenReturn(desiredCode);

        MensagemType other = mock(MensagemType.class);
        when(other.getTipoMensagem()).thenReturn(999);

        MensagemFactory factory = new MensagemFactory(List.of(other, matcher));

        MensagemType result = factory.create(TipoMensagem.MENSAGEM_ALERTA_RECONTATO);

        assertSame(matcher, result, "Deve retornar o primeiro MensagemType cujo código bate");
    }

    @Test
    void deveRetornarPrimeiraInstanciaComMultiplasValidacoes() {
        int code = TipoMensagem.CONTATO_INATIVO.getCodigo();
        MensagemType first = mock(MensagemType.class);
        when(first.getTipoMensagem()).thenReturn(code);
        MensagemType second = mock(MensagemType.class);
        when(second.getTipoMensagem()).thenReturn(code);

        MensagemFactory factory = new MensagemFactory(List.of(first, second));

        MensagemType result = factory.create(TipoMensagem.CONTATO_INATIVO);

        assertSame(first, result, "Quando mais de um bate, retorna sempre o primeiro");
        verify(second, never()).getTipoMensagem();
    }

    @Test
    void deveLancarExceptionQuandoNaoHaValidacao() {
        MensagemType t1 = mock(MensagemType.class);
        when(t1.getTipoMensagem()).thenReturn(123);
        MensagemFactory factory = new MensagemFactory(List.of(t1));

        assertThrows(
                EscolhaNaoIdentificadoException.class,
                () -> factory.create(TipoMensagem.MENSAGEM_DIRECIONAMENTO_VENDEDOR),
                "Deve lançar se nenhum MensagemType tiver o código solicitado"
        );

        verify(t1).getTipoMensagem();
    }
}