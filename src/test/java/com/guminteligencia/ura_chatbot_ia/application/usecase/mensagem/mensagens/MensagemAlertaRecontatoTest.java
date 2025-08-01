package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class MensagemAlertaRecontatoTest {

    private final MensagemAlertaRecontato mensagemAlertaRecontato = new MensagemAlertaRecontato();

    @Test
    void deveRetornaMensagemFormatoCorreto() {
        LocalDateTime fixedNow = LocalDateTime.of(2025, 8, 1, 9, 5);
        try (MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class)) {
            mocked.when(LocalDateTime::now).thenReturn(fixedNow);

            Cliente cliente = Cliente.builder().nome("Fulano").build();

            String resultado = mensagemAlertaRecontato.getMensagem("Carlos", cliente);

            String esperado =
                    "Cliente fez um recontato:\n" +
                            "Cliente: Fulano\n" +
                            "Vendedor: Carlos\n" +
                            "Hora: 09:05";

            assertEquals(esperado, resultado);
        }
    }

    @Test
    void deveRetornaMensagemAlertaRecontato() {
        int codigo = mensagemAlertaRecontato.getTipoMensagem();
        assertEquals(
                TipoMensagem.MENSAGEM_ALERTA_RECONTATO.getCodigo(),
                codigo
        );
    }
}