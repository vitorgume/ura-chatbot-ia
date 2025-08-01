package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MensagemDadosContatoVendedorTest {
    private final MensagemDadosContatoVendedor sut = new MensagemDadosContatoVendedor();

    @Test
    void deveRetornarMensagensComTodosCampos() {
        LocalDateTime fixed = LocalDateTime.of(2025, Month.AUGUST, 1, 7, 5);
        try (MockedStatic<LocalDateTime> mockTime = mockStatic(LocalDateTime.class)) {
            mockTime.when(LocalDateTime::now).thenReturn(fixed);

            Cliente cliente = mock(Cliente.class);
            when(cliente.getNome()).thenReturn("Ana");
            Segmento seg = mock(Segmento.class);
            when(seg.getDescricao()).thenReturn("Medicina");
            when(cliente.getSegmento()).thenReturn(seg);
            Regiao reg = mock(Regiao.class);
            when(reg.getDescricao()).thenReturn("Maringá");
            when(cliente.getRegiao()).thenReturn(reg);
            when(cliente.getTelefone()).thenReturn("+5511999000111");

            String msg = sut.getMensagem("VendedorX", cliente);

            String[] lines = msg.split("\n");
            assertEquals("Dados do contato acima:", lines[0]);
            assertEquals("Nome: Ana", lines[1]);
            assertEquals("Segmento: Medicina", lines[2]);
            assertEquals("Hora: 07:05", lines[3]);
            assertEquals("Região: Maringá", lines[4]);
            assertEquals("Telefone: +5511999000111", lines[5]);
        }
    }

    @Test
    void deveRetornarValoresPadaoComCampoNulos() {
        LocalDateTime fixed = LocalDateTime.of(2025, Month.AUGUST, 1, 23, 9);
        try (MockedStatic<LocalDateTime> mockTime = mockStatic(LocalDateTime.class)) {
            mockTime.when(LocalDateTime::now).thenReturn(fixed);

            Cliente cliente = mock(Cliente.class);
            when(cliente.getNome()).thenReturn(null);
            when(cliente.getSegmento()).thenReturn(null);
            when(cliente.getRegiao()).thenReturn(null);
            when(cliente.getTelefone()).thenReturn(null);

            String msg = sut.getMensagem("VendedorX", cliente);
            String[] lines = msg.split("\n");

            assertEquals("Dados do contato acima:", lines[0]);
            assertEquals("Nome: Nome não informado", lines[1]);
            assertEquals("Segmento: Segmento não informado", lines[2]);
            assertEquals("Hora: 23:09", lines[3]);
            assertEquals("Região: Região não informada", lines[4]);
            assertEquals("Telefone: Telefone não informado", lines[5]);
        }
    }

    @Test
    void deveRetornaNumeroCorreto() {
        int codigo = sut.getTipoMensagem();
        assertEquals(
                TipoMensagem.DADOS_CONTATO_VENDEDOR.getCodigo(),
                codigo
        );
    }

}