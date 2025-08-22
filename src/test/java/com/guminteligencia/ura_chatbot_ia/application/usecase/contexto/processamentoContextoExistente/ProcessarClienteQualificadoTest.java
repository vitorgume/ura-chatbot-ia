package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.application.usecase.AgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ClienteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.VendedorUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessarClienteQualificadoTest {

    @Mock
    private VendedorUseCase vendedorUseCase;

    @Mock
    private ClienteUseCase clienteUseCase;

    @Mock
    private MensagemUseCase mensagemUseCase;

    @Mock
    private MensagemBuilder mensagemBuilder;

    @Mock
    private AgenteUseCase agenteUseCase;

    @InjectMocks
    private ProcessarClienteQualificado processarClienteQualificado;

    @Mock
    private ConversaAgente conversaAgente;

    @Mock
    private Cliente originalCliente;

    @Mock
    private Cliente clienteSalvo;

    @Mock
    private Vendedor vendedor;

    private final String resposta = "conteudo quebrado QUALIFICADO:true extra";
    private final UUID originalId = UUID.randomUUID();
    private final String telSalvo = "+5511999111222";

    @Test
    void deveRetornarTrueSeRespostaContemQualificadoTrue() {
        assertTrue(processarClienteQualificado.deveProcessar(" QUALIFICADO:True  ", conversaAgente));
        assertTrue(processarClienteQualificado.deveProcessar("abcQualificado:truexyz", conversaAgente));
    }

    @Test
    void deveRetornaFalseParaNullSemFlagQualificadoFalse() {
        assertFalse(processarClienteQualificado.deveProcessar(null, conversaAgente));
        assertFalse(processarClienteQualificado.deveProcessar("qualificado:false", conversaAgente));
        assertFalse(processarClienteQualificado.deveProcessar("nada aqui", conversaAgente));
    }

    @Test
    void deveProcessarExecutarFluxoCompleto() {
        Qualificacao qual = new Qualificacao();
        qual.setNome("João");
        qual.setRegiao(Regiao.MARINGA.getCodigo());
        qual.setSegmento(Segmento.MEDICINA_SAUDE.getCodigo());
        qual.setDescricao_material("Descrito");
        when(agenteUseCase.enviarJsonTrasformacao(resposta)).thenReturn(qual);
        when(conversaAgente.getCliente()).thenReturn(originalCliente);
        when(originalCliente.getId()).thenReturn(originalId);

        ArgumentCaptor<Cliente> capCliente = ArgumentCaptor.forClass(Cliente.class);
        when(clienteUseCase.alterar(capCliente.capture(), eq(originalId)))
                .thenReturn(clienteSalvo);

        when(vendedorUseCase.escolherVendedor(clienteSalvo)).thenReturn(vendedor);
        when(vendedor.getNome()).thenReturn("Carlos");

        when(mensagemBuilder.getMensagem(
                TipoMensagem.MENSAGEM_DIRECIONAMENTO_VENDEDOR,
                "Carlos",
                null
        )).thenReturn("msg-dir");

        when(clienteSalvo.getTelefone()).thenReturn(telSalvo);

        processarClienteQualificado.processar(resposta, conversaAgente, originalCliente);

        InOrder inOrder = inOrder(
                agenteUseCase,
                clienteUseCase,
                vendedorUseCase,
                mensagemBuilder,
                mensagemUseCase,
                conversaAgente
        );

        inOrder.verify(agenteUseCase).enviarJsonTrasformacao(resposta);

        Cliente built = capCliente.getValue();
        assertEquals("João", built.getNome());
        assertEquals(1, built.getRegiao().getCodigo());
        assertEquals(1, built.getSegmento().getCodigo());
        assertEquals("Descrito", built.getDescricaoMaterial());

        inOrder.verify(clienteUseCase).alterar(any(), eq(originalId));

        inOrder.verify(vendedorUseCase).escolherVendedor(clienteSalvo);

        inOrder.verify(mensagemBuilder)
                .getMensagem(TipoMensagem.MENSAGEM_DIRECIONAMENTO_VENDEDOR, "Carlos", null);

        inOrder.verify(mensagemUseCase).enviarMensagem("msg-dir", telSalvo, false);

        inOrder.verify(mensagemUseCase).enviarContatoVendedor(vendedor, clienteSalvo);

        inOrder.verify(conversaAgente).setVendedor(vendedor);
        inOrder.verify(conversaAgente).setFinalizada(true);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void naoDeveProcessarFluxoCompletoLancandoException() {
        when(agenteUseCase.enviarJsonTrasformacao(resposta))
                .thenThrow(new RuntimeException("boom"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> processarClienteQualificado.processar(resposta, conversaAgente, originalCliente)
        );
        assertEquals("boom", ex.getMessage());

        verifyNoInteractions(clienteUseCase, vendedorUseCase, mensagemUseCase, mensagemBuilder);
    }

}