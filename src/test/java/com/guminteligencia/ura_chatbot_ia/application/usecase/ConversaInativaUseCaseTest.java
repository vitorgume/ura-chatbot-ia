package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.VendedorUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversaInativaUseCaseTest {

    @Mock
    private ConversaAgenteUseCase conversaAgenteUseCase;

    @Mock
    private VendedorUseCase vendedorUseCase;

    @Mock
    private MensagemUseCase mensagemUseCase;

    @Mock
    private MensagemBuilder mensagemBuilder;

    @Mock
    private CrmUseCase crmUseCase;

    @InjectMocks
    private ConversaInativaUseCase useCase;

    @Test
    void naoDeveProcessarQuandoNaoExistiremConversas() {
        when(conversaAgenteUseCase.listarNaoFinalizados()).thenReturn(List.of());

        useCase.verificaAusenciaDeMensagem();

        verify(conversaAgenteUseCase).listarNaoFinalizados();
        verifyNoInteractions(vendedorUseCase, mensagemUseCase, mensagemBuilder);
    }

    @Test
    void naoDeveProcessarQuandoConversasNaoEstiveremAtrasadas() {
        LocalDateTime now = LocalDateTime.of(2025, 8, 4, 12, 0);
        try (MockedStatic<LocalDateTime> mockNow = mockStatic(LocalDateTime.class)) {
            mockNow.when(LocalDateTime::now).thenReturn(now);

            ConversaAgente conv = mock(ConversaAgente.class);
            when(conv.getDataUltimaMensagem()).thenReturn(now.minusMinutes(10));
            when(conversaAgenteUseCase.listarNaoFinalizados()).thenReturn(List.of(conv));

            useCase.verificaAusenciaDeMensagem();

            verify(conversaAgenteUseCase).listarNaoFinalizados();
            verifyNoInteractions(vendedorUseCase, mensagemUseCase, mensagemBuilder);
        }
    }

    @Test
    void deveProcessarConversasAtrasadas() {
        LocalDateTime now = LocalDateTime.of(2025, 8, 4, 12, 0);
        try (MockedStatic<LocalDateTime> mockNow = mockStatic(LocalDateTime.class)) {
            mockNow.when(LocalDateTime::now).thenReturn(now);

            ConversaAgente conv = mock(ConversaAgente.class);
            when(conv.getDataUltimaMensagem()).thenReturn(now.minusMinutes(40));

            Cliente cliente = Cliente.builder()
                    .id(UUID.randomUUID())
                    .telefone("+55999999999")
                    .build();
            when(conv.getCliente()).thenReturn(cliente);

            when(conversaAgenteUseCase.listarNaoFinalizados())
                    .thenReturn(List.of(conv));

            Vendedor vendedor = mock(Vendedor.class);
            when(vendedorUseCase.roletaVendedoresConversaInativa(cliente))
                    .thenReturn(vendedor);

            doNothing().when(crmUseCase).atualizarCrm(Mockito.any(), Mockito.any(), Mockito.any());

            useCase.verificaAusenciaDeMensagem();

            InOrder ord = inOrder(conv, vendedorUseCase, crmUseCase, conversaAgenteUseCase);
            ord.verify(conv).setFinalizada(true);
            ord.verify(vendedorUseCase).roletaVendedoresConversaInativa(cliente);
            ord.verify(conv).setVendedor(vendedor);
            ord.verify(conv).setInativa(true);
            ord.verify(crmUseCase).atualizarCrm(Mockito.any(), Mockito.any(), Mockito.any());
            ord.verify(conversaAgenteUseCase).salvar(conv);
            ord.verifyNoMoreInteractions();
        }
    }
}