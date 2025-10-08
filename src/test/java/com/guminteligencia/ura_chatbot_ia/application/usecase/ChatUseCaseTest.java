package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.ChatNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.ChatGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Chat;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.MensagemConversa;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatUseCaseTest {

    @Mock
    private ChatGateway gateway;

    @Mock
    private MensagemConversaUseCase mensagemConversaUseCase;

    @Mock
    private ConversaAgenteUseCase conversaAgenteUseCase;

    // Deixo declarado para manter o "shape" do seu exemplo;
    // mas a instância final do useCase será criada manualmente no setup por causa do URL_CHAT.
    @InjectMocks
    private ChatUseCase useCase;

    private static final String BASE_URL = "https://example.com/chat/";

    private UUID idChat;
    private UUID idConversa;
    private UUID idGerado;
    private Cliente cliente;
    private ConversaAgente conversaAgente;
    private List<MensagemConversa> mensagens;

    @BeforeEach
    void setup() {
        // Instanciação manual para injetar a BASE_URL do construtor
        useCase = new ChatUseCase(gateway, mensagemConversaUseCase, conversaAgenteUseCase, BASE_URL);

        idChat = UUID.randomUUID();
        idConversa = UUID.randomUUID();
        idGerado = UUID.randomUUID();

        cliente = Cliente.builder()
                .id(UUID.randomUUID())
                .build();

        conversaAgente = ConversaAgente.builder()
                .id(idConversa)
                .cliente(cliente)
                .build();

        mensagens = List.of(
                MensagemConversa.builder().id(UUID.randomUUID()).build()
        );
    }

    // -------------------- acessar(UUID) --------------------

    @Test
    void deveRetornarChatQuandoEncontrado() {
        Chat chatSalvo = Chat.builder().id(idChat).build();
        when(gateway.consultarPorId(idChat)).thenReturn(Optional.of(chatSalvo));

        Chat resultado = useCase.acessar(idChat);

        assertNotNull(resultado);
        assertSame(chatSalvo, resultado);
        verify(gateway).consultarPorId(idChat);
        verifyNoMoreInteractions(gateway, mensagemConversaUseCase, conversaAgenteUseCase);
    }

    @Test
    void deveLancarChatNaoEncontradoQuandoNaoExistir() {
        when(gateway.consultarPorId(idChat)).thenReturn(Optional.empty());

        assertThrows(ChatNaoEncontradoException.class, () -> useCase.acessar(idChat));

        verify(gateway).consultarPorId(idChat);
        verifyNoMoreInteractions(gateway, mensagemConversaUseCase, conversaAgenteUseCase);
    }

    @Test
    void devePropagarExcecaoDoGatewayNoAcessar() {
        when(gateway.consultarPorId(idChat)).thenThrow(new DataProviderException("falha", null));

        assertThrows(DataProviderException.class, () -> useCase.acessar(idChat));

        verify(gateway).consultarPorId(idChat);
        verifyNoMoreInteractions(gateway, mensagemConversaUseCase, conversaAgenteUseCase);
    }

    // -------------------- criar(UUID) --------------------

    @Test
    void deveCriarChatSalvarERetornarUrlCompleta() {
        when(conversaAgenteUseCase.consultarPorId(idConversa)).thenReturn(conversaAgente);
        when(mensagemConversaUseCase.listarPelaConversa(idConversa)).thenReturn(mensagens);

        when(gateway.salvar(any(Chat.class))).thenAnswer(invocation -> {
            Chat input = invocation.getArgument(0);
            return Chat.builder()
                    .id(idGerado)
                    .dataCriacao(input.getDataCriacao())
                    .cliente(input.getCliente())
                    .mensagensChat(input.getMensagensChat())
                    .build();
        });

        LocalDateTime antes = LocalDateTime.now();
        String url = useCase.criar(idConversa);
        LocalDateTime depois = LocalDateTime.now();

        assertEquals(BASE_URL + idGerado.toString(), url);

        ArgumentCaptor<Chat> cap = ArgumentCaptor.forClass(Chat.class);
        verify(gateway).salvar(cap.capture());
        Chat enviado = cap.getValue();

        assertNotNull(enviado);
        assertNotNull(enviado.getDataCriacao());
        assertFalse(enviado.getDataCriacao().isBefore(antes));
        assertFalse(enviado.getDataCriacao().isAfter(depois));
        assertSame(cliente, enviado.getCliente());
        assertEquals(mensagens, enviado.getMensagensChat());

        InOrder ordem = inOrder(conversaAgenteUseCase, mensagemConversaUseCase, gateway);
        ordem.verify(conversaAgenteUseCase).consultarPorId(idConversa);
        ordem.verify(mensagemConversaUseCase).listarPelaConversa(idConversa);
        ordem.verify(gateway).salvar(any(Chat.class));
        verifyNoMoreInteractions(gateway, mensagemConversaUseCase, conversaAgenteUseCase);
    }

    @Test
    void devePropagarQuandoConsultarConversaFalha() {
        RuntimeException erro = new RuntimeException("falha consulta conversa");
        when(conversaAgenteUseCase.consultarPorId(idConversa)).thenThrow(erro);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> useCase.criar(idConversa));
        assertSame(erro, thrown);

        verify(conversaAgenteUseCase).consultarPorId(idConversa);
        verifyNoInteractions(mensagemConversaUseCase);
        verifyNoInteractions(gateway);
    }

    @Test
    void devePropagarQuandoListarMensagensFalha() {
        when(conversaAgenteUseCase.consultarPorId(idConversa)).thenReturn(conversaAgente);
        when(mensagemConversaUseCase.listarPelaConversa(idConversa))
                .thenThrow(new RuntimeException("falha listar mensagens"));

        assertThrows(RuntimeException.class, () -> useCase.criar(idConversa));

        InOrder ordem = inOrder(conversaAgenteUseCase, mensagemConversaUseCase);
        ordem.verify(conversaAgenteUseCase).consultarPorId(idConversa);
        ordem.verify(mensagemConversaUseCase).listarPelaConversa(idConversa);

        verifyNoInteractions(gateway);
    }

    @Test
    void devePropagarQuandoSalvarFalha() {
        when(conversaAgenteUseCase.consultarPorId(idConversa)).thenReturn(conversaAgente);
        when(mensagemConversaUseCase.listarPelaConversa(idConversa)).thenReturn(Collections.emptyList());
        when(gateway.salvar(any(Chat.class))).thenThrow(new DataProviderException("erro ao salvar", null));

        assertThrows(DataProviderException.class, () -> useCase.criar(idConversa));

        InOrder ordem = inOrder(conversaAgenteUseCase, mensagemConversaUseCase, gateway);
        ordem.verify(conversaAgenteUseCase).consultarPorId(idConversa);
        ordem.verify(mensagemConversaUseCase).listarPelaConversa(idConversa);
        ordem.verify(gateway).salvar(any(Chat.class));
    }

    @Test
    void sanidadeDataCriacaoProximaDoAgora() {
        when(conversaAgenteUseCase.consultarPorId(idConversa)).thenReturn(conversaAgente);
        when(mensagemConversaUseCase.listarPelaConversa(idConversa)).thenReturn(Collections.emptyList());
        when(gateway.salvar(any(Chat.class))).thenReturn(Chat.builder().id(UUID.randomUUID()).build());

        LocalDateTime t0 = LocalDateTime.now();
        useCase.criar(idConversa);
        LocalDateTime t1 = LocalDateTime.now();

        ArgumentCaptor<Chat> cap = ArgumentCaptor.forClass(Chat.class);
        verify(gateway).salvar(cap.capture());
        LocalDateTime dataCriacao = cap.getValue().getDataCriacao();

        assertNotNull(dataCriacao);
        assertFalse(dataCriacao.isBefore(t0.minusSeconds(1)));
        assertFalse(dataCriacao.isAfter(t1.plusSeconds(5)));
    }

}