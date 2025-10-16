package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guminteligencia.ura_chatbot_ia.application.gateways.AgenteGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.MensagemAgenteDto;
import com.guminteligencia.ura_chatbot_ia.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgenteUseCaseTest {

    @Mock
    private AgenteGateway gateway;

    @InjectMocks
    private AgenteUseCase useCase;

    private Cliente cliente;
    private ConversaAgente conversa;
    private UUID clienteId;
    private UUID conversaId;

    @BeforeEach
    void setup() {
        clienteId = UUID.randomUUID();
        conversaId = UUID.randomUUID();

        cliente = Cliente.builder()
                .id(clienteId)
                .build();

        conversa = mock(ConversaAgente.class);
    }

    @Test
    void deveEnviarMensagemConcatenarListaEChamarGateway() {
        List<String> msgs = List.of("oi", "tudo bem?", "até logo");
        String esperadoConcat = "oi, tudo bem?, até logo";
        MensagemAgenteDto capturado;
        when(gateway.enviarMensagem(any())).thenReturn("resp-ok");
        when(conversa.getId()).thenReturn(conversaId);

        String resposta = useCase.enviarMensagem(cliente, conversa, msgs);

        assertEquals("resp-ok", resposta);

        ArgumentCaptor<MensagemAgenteDto> dtoCap = ArgumentCaptor.forClass(MensagemAgenteDto.class);
        verify(gateway).enviarMensagem(dtoCap.capture());
        capturado = dtoCap.getValue();

        assertEquals(clienteId.toString(), capturado.getClienteId());
        assertEquals(conversaId.toString(), capturado.getConversaId());
        assertEquals(esperadoConcat, capturado.getMensagem());
    }

    @Test
    void deveEnviarMensagemComListaVaziaConcatenacaoVazia() {
        when(gateway.enviarMensagem(any())).thenReturn("vazio");
        when(conversa.getId()).thenReturn(conversaId);

        String resp = useCase.enviarMensagem(cliente, conversa, List.of());

        assertEquals("vazio", resp);
        ArgumentCaptor<MensagemAgenteDto> cap = ArgumentCaptor.forClass(MensagemAgenteDto.class);
        verify(gateway).enviarMensagem(cap.capture());
        assertEquals("", cap.getValue().getMensagem());
    }

    @Test
    void deveLancarRuntimeExceptionQuandoGatewayRetornarJsonInvalidoEmTransformacao() {
        String texto = "qualquer texto";
        when(gateway.enviarJsonTrasformacao(texto)).thenReturn("not a json");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.enviarJsonTrasformacao(texto));
        assertTrue(ex.getMessage().contains("Erro ao tentar mapear JSON da IA"));
    }

    @Test
    void deveRetornarQualificacaoQuandoJsonValido() throws Exception {
        String texto = "texto";
        Qualificacao qual = new Qualificacao();
        qual.setNome("Ana");
        qual.setRegiao(Regiao.MARINGA.getCodigo());
        qual.setSegmento(Segmento.MEDICINA_SAUDE.getCodigo());
        qual.setDescricaoMaterial("desc");
        String json = new ObjectMapper().writeValueAsString(qual);

        when(gateway.enviarJsonTrasformacao(texto)).thenReturn(json);

        Qualificacao resultado = useCase.enviarJsonTrasformacao(texto);

        assertNotNull(resultado);
        assertEquals("Ana", resultado.getNome());
        assertEquals(Regiao.MARINGA.getCodigo(), resultado.getRegiao());
        assertEquals(Segmento.MEDICINA_SAUDE.getCodigo(), resultado.getSegmento());
        assertEquals("desc", resultado.getDescricaoMaterial());
    }

    @Test
    void deveRetornarQualificacaoQuandoGatewayRetornaJsonDentroDeStringTextual() throws Exception {
        String texto = "texto";

        // Monta um JSON válido como string
        Qualificacao qual = new Qualificacao();
        qual.setNome("Ana");
        qual.setRegiao(Regiao.MARINGA.getCodigo());
        qual.setSegmento(Segmento.MEDICINA_SAUDE.getCodigo());
        qual.setDescricaoMaterial("desc");

        // JSON "normal"
        String innerJson = new ObjectMapper().writeValueAsString(qual);
        // Agora embrulha como STRING JSON (nó textual): "\"{...}\""
        String wrappedAsTextNode = new ObjectMapper().writeValueAsString(innerJson);

        when(gateway.enviarJsonTrasformacao(texto)).thenReturn(wrappedAsTextNode);

        Qualificacao resultado = useCase.enviarJsonTrasformacao(texto);

        assertNotNull(resultado);
        assertEquals("Ana", resultado.getNome());
        assertEquals(Regiao.MARINGA.getCodigo(), resultado.getRegiao());
        assertEquals(Segmento.MEDICINA_SAUDE.getCodigo(), resultado.getSegmento());
        assertEquals("desc", resultado.getDescricaoMaterial());
    }

    @Test
    void deveLancarRuntimeExceptionQuandoJsonTextualTemConteudoInternoInvalido() throws Exception {
        String texto = "qualquer";

        // Primeiro parse vira nó textual: "\"not a json\""
        String wrappedInvalid = new ObjectMapper().writeValueAsString("not a json");

        when(gateway.enviarJsonTrasformacao(texto)).thenReturn(wrappedInvalid);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.enviarJsonTrasformacao(texto));

        assertTrue(ex.getMessage().contains("Erro ao tentar mapear JSON da IA"));
    }
}