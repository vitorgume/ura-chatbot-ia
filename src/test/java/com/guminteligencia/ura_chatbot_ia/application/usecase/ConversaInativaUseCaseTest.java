package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.VendedorUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.StatusConversa;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import org.junit.jupiter.api.BeforeEach;
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

    private ConversaInativaUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ConversaInativaUseCase(
                conversaAgenteUseCase,
                vendedorUseCase,
                crmUseCase,
                mensagemUseCase,
                mensagemBuilder,
                "dev" // thresholds dev: G1=10s, G2=20s
        );
    }

    @Test
    void naoDeveProcessarQuandoNaoExistiremConversas() {
        when(conversaAgenteUseCase.listarNaoFinalizados()).thenReturn(List.of());

        useCase.verificaAusenciaDeMensagem();

        verify(conversaAgenteUseCase).listarNaoFinalizados();
        verifyNoInteractions(vendedorUseCase, mensagemUseCase, mensagemBuilder, crmUseCase);
    }

    @Test
    void naoDeveProcessarQuandoConversasNaoEstiveremAtrasadas() {
        LocalDateTime now = LocalDateTime.of(2025, 8, 4, 12, 0);
        try (MockedStatic<LocalDateTime> mockNow = mockStatic(LocalDateTime.class)) {
            mockNow.when(LocalDateTime::now).thenReturn(now);

            // G1 não atrasado: inativo == null e dataUltimaMensagem dentro de 10s
            ConversaAgente conv = ConversaAgente.builder()
                    .id(UUID.randomUUID())
                    .cliente(Cliente.builder()
                            .id(UUID.randomUUID())
                            .nome("teste")
                            .build())
                    .vendedor(Vendedor.builder().id(1L).build())
                    .dataCriacao(now)
                    .dataUltimaMensagem(now.minusSeconds(5)) // dentro do limite de 10s
                    .recontato(false)
                    .status(null)
                    .build();

            when(conversaAgenteUseCase.listarNaoFinalizados()).thenReturn(List.of(conv));

            useCase.verificaAusenciaDeMensagem();

            verify(conversaAgenteUseCase).listarNaoFinalizados();
            verifyNoInteractions(vendedorUseCase, mensagemUseCase, mensagemBuilder, crmUseCase);
        }
    }

    @Test
    void deveProcessarConversasAtrasadas_G1_inativoNull_maiorQue10s() {
        LocalDateTime now = LocalDateTime.of(2025, 8, 4, 12, 0);
        try (MockedStatic<LocalDateTime> mockNow = mockStatic(LocalDateTime.class)) {
            mockNow.when(LocalDateTime::now).thenReturn(now);

            // Mockamos a conversa para verificar setters
            ConversaAgente conv = mock(ConversaAgente.class);

            // Estado de leitura (G1): inativo == null e atraso > 10s
            when(conv.getStatus()).thenReturn(StatusConversa.ANDAMENTO);
            when(conv.getDataUltimaMensagem()).thenReturn(now.minusSeconds(15)); // > 10s

            Cliente cliente = Cliente.builder()
                    .id(UUID.randomUUID())
                    .nome("teste")
                    .telefone("+55999999999")
                    .build();
            when(conv.getCliente()).thenReturn(cliente);

            when(conversaAgenteUseCase.listarNaoFinalizados()).thenReturn(List.of(conv));

            // Mensagem G1
            when(mensagemBuilder.getMensagem(eq(TipoMensagem.RECONTATO_INATIVO_G1), any(), any()))
                    .thenReturn("msg-recontato-g1");

            useCase.verificaAusenciaDeMensagem();

            // Verificações do fluxo G1:
            InOrder inOrder = inOrder(conv, mensagemBuilder, mensagemUseCase, conversaAgenteUseCase);
            inOrder.verify(conv).getDataUltimaMensagem();
            inOrder.verify(conv).getStatus();

            inOrder.verify(conv).setStatus(StatusConversa.INATIVO_G1);
            inOrder.verify(mensagemBuilder).getMensagem(eq(TipoMensagem.RECONTATO_INATIVO_G1), isNull(), isNull());
            inOrder.verify(mensagemUseCase).enviarMensagem(eq("msg-recontato-g1"), eq(cliente.getTelefone()), eq(false));
            inOrder.verify(conv).setDataUltimaMensagem(now);
            inOrder.verify(conversaAgenteUseCase).salvar(conv);
            inOrder.verifyNoMoreInteractions();

            // Não deve envolver vendedor nem CRM no G1
            verifyNoInteractions(vendedorUseCase, crmUseCase);
        }
    }

    @Test
    void deveProcessarConversasAtrasadas_G2_inativoNaoNull_maiorQue20s() {
        LocalDateTime now = LocalDateTime.of(2025, 8, 4, 12, 0);
        try (MockedStatic<LocalDateTime> mockNow = mockStatic(LocalDateTime.class)) {
            mockNow.when(LocalDateTime::now).thenReturn(now);

            // Mockamos a conversa para verificar setters e fluxo G2
            ConversaAgente conv = mock(ConversaAgente.class);

            // Estado de leitura (G2): já tem inativo != null e atraso > 20s
            when(conv.getStatus()).thenReturn(StatusConversa.INATIVO_G1);
            when(conv.getDataUltimaMensagem()).thenReturn(now.minusSeconds(25)); // > 20s

            Cliente cliente = Cliente.builder()
                    .id(UUID.randomUUID())
                    .nome("teste")
                    .telefone("+55999999999")
                    .build();
            when(conv.getCliente()).thenReturn(cliente);

            when(conversaAgenteUseCase.listarNaoFinalizados()).thenReturn(List.of(conv));

            Vendedor vendedor = Vendedor.builder().id(1L).nome("Nome teste").build();
            when(vendedorUseCase.roletaVendedoresConversaInativa(cliente)).thenReturn(vendedor);

            doNothing().when(crmUseCase).atualizarCrm(any(), any(), any());

            useCase.verificaAusenciaDeMensagem();

            // Verificações do fluxo G2 (ordem relevante conforme código)
            InOrder inOrder = inOrder(conv, vendedorUseCase, crmUseCase, mensagemUseCase, conversaAgenteUseCase);
            inOrder.verify(conv).getDataUltimaMensagem();
            inOrder.verify(conv).getStatus();

            inOrder.verify(conv).setStatus(StatusConversa.INATIVO_G2);
            inOrder.verify(conv).setFinalizada(true);
            inOrder.verify(vendedorUseCase).roletaVendedoresConversaInativa(cliente);
            inOrder.verify(conv).setVendedor(vendedor);
            inOrder.verify(crmUseCase).atualizarCrm(eq(vendedor), eq(cliente), eq(conv));
            inOrder.verify(mensagemUseCase).enviarContatoVendedor(eq(vendedor), eq(cliente));
            inOrder.verify(conversaAgenteUseCase).salvar(conv);
            inOrder.verifyNoMoreInteractions();

            // G2 não envia mensagem de recontato nem mexe no dataUltimaMensagem via mensagem
            verifyNoInteractions(mensagemBuilder);
        }
    }
}