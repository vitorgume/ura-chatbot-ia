package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.LeadNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.CrmGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.CardDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.CustomFieldDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.CustomFieldValueDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.SessaoArquivoDto;
import com.guminteligencia.ura_chatbot_ia.domain.*;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrmUseCaseTest {

    @Mock
    private CrmGateway gateway;

    @Mock
    private ChatUseCase chatUseCase;

    @Mock
    private MidiaClienteUseCase midiaClienteUseCase;

    @InjectMocks
    private com.guminteligencia.ura_chatbot_ia.application.usecase.CrmUseCase useCase;

    @Mock
    private Vendedor vendedor;

    @Mock
    private Cliente cliente;

    @Mock
    private ConversaAgente conversaAgente;

    private final String tel = "+5511999999999";
    private final Integer idLead = 12345;
    private final String urlChat = "https://chat.example/sala/abc";

    // --------------- atualizarCrm ----------------

    @Test
    void atualizarCrm_deveAtualizarComMidia_eClienteInativo() {
        when(cliente.getTelefone()).thenReturn(tel);
        when(cliente.getDescricaoMaterial()).thenReturn("desc-material");
        when(cliente.getEnderecoReal()).thenReturn("Rua A, 123");
        when(cliente.isInativo()).thenReturn(true);

        Segmento segmento = mock(Segmento.class);
        when(segmento.getIdCrm()).thenReturn(10);
        when(cliente.getSegmento()).thenReturn(segmento);

        Regiao regiao = mock(Regiao.class);
        when(regiao.getIdCrm()).thenReturn(20);
        when(cliente.getRegiao()).thenReturn(regiao);

        when(vendedor.getIdVendedorCrm()).thenReturn(999);
        when(conversaAgente.getId()).thenReturn(UUID.randomUUID());
        when(chatUseCase.criar(any())).thenReturn(urlChat);

        MidiaCliente midia = mock(MidiaCliente.class);
        List<String> urls = List.of("file:///tmp/a.mp4", "file:///tmp/b.mp4");
        when(midia.getUrlMidias()).thenReturn(urls);
        when(midiaClienteUseCase.consultarMidiaPeloTelefoneCliente(tel))
                .thenReturn(Optional.of(midia));

        when(gateway.consultaLeadPeloTelefone(tel)).thenReturn(Optional.of(idLead));
        SessaoArquivoDto s1 = mock(SessaoArquivoDto.class);
        SessaoArquivoDto s2 = mock(SessaoArquivoDto.class);
        when(gateway.criarSessaoArquivo(urls.get(0))).thenReturn(s1);
        when(gateway.criarSessaoArquivo(urls.get(1))).thenReturn(s2);
        when(gateway.enviarArquivoParaUpload(s1, urls.get(0))).thenReturn("uuid-1");
        when(gateway.enviarArquivoParaUpload(s2, urls.get(1))).thenReturn("uuid-2");
        doNothing().when(gateway).atualizarCard(any(CardDto.class), eq(idLead));


        useCase.atualizarCrm(vendedor, cliente, conversaAgente);

        // verificações de upload/anexo
        verify(gateway).criarSessaoArquivo(urls.get(0));
        verify(gateway).criarSessaoArquivo(urls.get(1));
        verify(gateway).enviarArquivoParaUpload(s1, urls.get(0));
        verify(gateway).enviarArquivoParaUpload(s2, urls.get(1));
        verify(gateway).anexarArquivoLead("uuid-1", idLead);
        verify(gateway).anexarArquivoLead("uuid-2", idLead);

        // captura do CardDto para validar os campos
        ArgumentCaptor<CardDto> captor = ArgumentCaptor.forClass(CardDto.class);
        verify(gateway).atualizarCard(captor.capture(), eq(idLead));

        CardDto card = captor.getValue();
        assertNotNull(card);
        assertEquals(999, card.getResponsibleUserId());
        assertEquals(93572343, card.getStatusId());

        // custom fields: ids e valores
        Map<Integer, CustomFieldDto> byId = indexByFieldId(card.getCustomFieldsValues());
        assertEquals(5, byId.size());
        // 1484843 - descricao_material (value)
        assertEquals("desc-material", onlyValue(byId.get(1484843)));
        // 1486843 - segmento (enum_id)
        assertEquals(10, onlyEnum(byId.get(1486843)));
        // 1486845 - regiao (enum_id)
        assertEquals(20, onlyEnum(byId.get(1486845)));
        // 1486847 - endereco_real (value)
        assertEquals("Rua A, 123", onlyValue(byId.get(1486847)));
        // 1486849 - url_historico (value)
        assertEquals(urlChat, onlyValue(byId.get(1486849)));

        // tag (cliente inativo => id 111143)
        @SuppressWarnings("unchecked")
        Map<String, Object> embedded = (Map<String, Object>) card.getEmbedded();
        assertNotNull(embedded);
        assertTrue(embedded.containsKey("tags"));
        List<?> tags = (List<?>) embedded.get("tags");
        assertEquals(1, tags.size());
        @SuppressWarnings("unchecked")
        Map<String, ?> tag = (Map<String, ?>) tags.get(0);
        assertEquals(111143, tag.get("id"));
    }

    @Test
    void atualizarCrm_deveAtualizarSemMidia_eClienteAtivo() {
        // dados base
        String tel = "+5511999999999";
        String urlChat = "https://chat/fake";
        int idLead = 42;

        // cliente ativo
        when(cliente.isInativo()).thenReturn(false);
        when(cliente.getTelefone()).thenReturn(tel);
        when(cliente.getDescricaoMaterial()).thenReturn("desc-material");
        when(cliente.getEnderecoReal()).thenReturn("Rua A, 123");

        // segmento e região (evita NPE em getSegmento().getIdCrm())
        Segmento segmento = mock(Segmento.class);
        when(segmento.getIdCrm()).thenReturn(10);
        when(cliente.getSegmento()).thenReturn(segmento);

        Regiao regiao = mock(Regiao.class);
        when(regiao.getIdCrm()).thenReturn(20);
        when(cliente.getRegiao()).thenReturn(regiao);

        // vendedor e chat
        when(vendedor.getIdVendedorCrm()).thenReturn(999);
        UUID convId = UUID.randomUUID();
        when(conversaAgente.getId()).thenReturn(convId);
        when(chatUseCase.criar(convId)).thenReturn(urlChat);

        // sem mídia
        when(midiaClienteUseCase.consultarMidiaPeloTelefoneCliente(tel))
                .thenReturn(Optional.empty());

        // lead encontrado
        when(gateway.consultaLeadPeloTelefone(tel)).thenReturn(Optional.of(idLead));

        // patch OK
        doNothing().when(gateway).atualizarCard(any(CardDto.class), eq(idLead));

        // act
        useCase.atualizarCrm(vendedor, cliente, conversaAgente);

        // não deve fazer upload algum
        verify(gateway, never()).criarSessaoArquivo(anyString());
        verify(gateway, never()).enviarArquivoParaUpload(any(), anyString());
        verify(gateway, never()).anexarArquivoLead(anyString(), anyInt());

        // valida tag de cliente ATIVO (117527)
        ArgumentCaptor<CardDto> captor = ArgumentCaptor.forClass(CardDto.class);
        verify(gateway).atualizarCard(captor.capture(), eq(idLead));
        CardDto card = captor.getValue();

        @SuppressWarnings("unchecked")
        Map<String, Object> embedded = (Map<String, Object>) card.getEmbedded();
        List<?> tags = (List<?>) embedded.get("tags");
        @SuppressWarnings("unchecked")
        Map<String, ?> tag = (Map<String, ?>) tags.get(0);
        assertEquals(117527, tag.get("id"));
    }


    @Test
    void atualizarCrm_devePropagarExcecaoDeAtualizarCard() {

        // dados base
        String tel = "+5511999999999";
        String urlChat = "https://chat/fake";
        int idLead = 42;

        // cliente ativo
        when(cliente.isInativo()).thenReturn(false);
        when(cliente.getTelefone()).thenReturn(tel);
        when(cliente.getDescricaoMaterial()).thenReturn("desc-material");
        when(cliente.getEnderecoReal()).thenReturn("Rua A, 123");

        // segmento e região (evita NPE em getSegmento().getIdCrm())
        Segmento segmento = mock(Segmento.class);
        when(segmento.getIdCrm()).thenReturn(10);
        when(cliente.getSegmento()).thenReturn(segmento);

        Regiao regiao = mock(Regiao.class);
        when(regiao.getIdCrm()).thenReturn(20);
        when(cliente.getRegiao()).thenReturn(regiao);

        // vendedor e chat
        when(vendedor.getIdVendedorCrm()).thenReturn(999);
        UUID convId = UUID.randomUUID();
        when(conversaAgente.getId()).thenReturn(convId);
        when(chatUseCase.criar(convId)).thenReturn(urlChat);

        // sem mídia
        when(midiaClienteUseCase.consultarMidiaPeloTelefoneCliente(tel))
                .thenReturn(Optional.empty());

        // lead encontrado
        when(gateway.consultaLeadPeloTelefone(tel)).thenReturn(Optional.of(idLead));

        when(cliente.isInativo()).thenReturn(false);


        doThrow(new DataProviderException("patch-fail", null))
                .when(gateway).atualizarCard(any(CardDto.class), eq(idLead));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> useCase.atualizarCrm(vendedor, cliente, conversaAgente)
        );
        assertEquals("patch-fail", ex.getMessage());
    }

    // --------------- consultaLeadPeloTelefone ----------------

    @Test
    void consultaLeadPeloTelefone_deveRetornarId() {
        String tel = "+5511999999999";

        int idLead = 42;

        // lead encontrado
        when(gateway.consultaLeadPeloTelefone(tel)).thenReturn(Optional.of(idLead));

        Integer out = useCase.consultaLeadPeloTelefone(tel);
        assertEquals(idLead, out);
        verify(gateway).consultaLeadPeloTelefone(tel);
    }

    @Test
    void consultaLeadPeloTelefone_deveLancarLeadNaoEncontradoQuandoEmpty() {
        when(gateway.consultaLeadPeloTelefone(tel)).thenReturn(Optional.empty());
        assertThrows(
                LeadNaoEncontradoException.class,
                () -> useCase.consultaLeadPeloTelefone(tel)
        );
    }

    // --------------- carregarArquivo ----------------

    @Test
    void carregarArquivo_deveChamarFluxoDeUploadEAnexar() {
        String url = "file:///tmp/video.mp4";
        SessaoArquivoDto sessao = mock(SessaoArquivoDto.class);
        when(gateway.criarSessaoArquivo(url)).thenReturn(sessao);
        when(gateway.enviarArquivoParaUpload(sessao, url)).thenReturn("uuid-arq");

        useCase.carregarArquivo(url, idLead);

        InOrder in = inOrder(gateway);
        in.verify(gateway).criarSessaoArquivo(url);
        in.verify(gateway).enviarArquivoParaUpload(sessao, url);
        in.verify(gateway).anexarArquivoLead("uuid-arq", idLead);
    }

    // ----------------- helpers -----------------

    private static Map<Integer, CustomFieldDto> indexByFieldId(List<CustomFieldDto> list) {
        Map<Integer, CustomFieldDto> m = new HashMap<>();
        for (CustomFieldDto c : list) m.put(c.getFieldId(), c);
        return m;
    }

    private static Object onlyValue(CustomFieldDto c) {
        assertNotNull(c);
        assertNotNull(c.getValues());
        assertFalse(c.getValues().isEmpty());
        CustomFieldValueDto v = c.getValues().get(0);
        return v.getValue();
    }

    private static Integer onlyEnum(CustomFieldDto c) {
        assertNotNull(c);
        assertNotNull(c.getValues());
        assertFalse(c.getValues().isEmpty());
        CustomFieldValueDto v = c.getValues().get(0);
        return v.getEnumId();
    }

}