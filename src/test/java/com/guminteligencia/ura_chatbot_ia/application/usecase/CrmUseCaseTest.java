package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.LeadNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.CrmGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.CardDto;
import com.guminteligencia.ura_chatbot_ia.domain.*;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

    @InjectMocks
    private CrmUseCase useCase;

    @Mock
    private Vendedor vendedor;

    @Mock
    private Cliente cliente;

    @Mock
    private ConversaAgente conversaAgente;

    @BeforeEach
    void setUp() {
        useCase = new CrmUseCase(
                gateway,
                chatUseCase,
                "prod"
        );

        conversaAgente = ConversaAgente.builder()
                .id(UUID.randomUUID())
                .status(StatusConversa.INATIVO_G1)
                .build();
    }

    private final String tel = "+5511999999999";
    private final Integer idLead = 12345;
    private final String urlChat = "https://chat.example/sala/abc";

    // --------------- atualizarCrm ----------------

    @Test
    void atualizarCrm_deveAtualizarSemMidia_eClienteAtivo() {
        // dados base
        String tel = "+5511999999999";
        String urlChat = "https://chat/fake";
        int idLead = 42;
        ContactDto lead = ContactDto.builder()
                .id(10)
                .embedded(new ContactDto.Embedded(
                                null, null, List.of(new ContactDto.LeadRef(idLead))
                        )
                ).build();

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
        ;
//        when(chatUseCase.criar(conversaAgente.getId())).thenReturn(urlChat);

        // lead encontrado
        when(gateway.consultaLeadPeloTelefone(tel)).thenReturn(Optional.of(lead));

        // patch OK
        doNothing().when(gateway).atualizarCard(any(CardDto.class), eq(idLead));
        doNothing().when(gateway).atualizarContato(eq(10), any());

        // act
        useCase.atualizarCrm(vendedor, cliente, conversaAgente);

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
        @SuppressWarnings("unchecked")
        Map<String, ?> tagIdentificador = (Map<String, ?>) tags.get(1);
        assertEquals(126472, tagIdentificador.get("id"));
    }


    @Test
    void atualizarCrm_devePropagarExcecaoDeAtualizarCard() {

        // dados base
        String tel = "+5511999999999";
        String urlChat = "https://chat/fake";
        int idLead = 42;
        ContactDto lead = ContactDto.builder()
                .id(10)
                .embedded(new ContactDto.Embedded(
                                null, null, List.of(new ContactDto.LeadRef(idLead))
                        )
                ).build();

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

//        when(chatUseCase.criar(conversaAgente.getId())).thenReturn(urlChat);

        // lead encontrado
        when(gateway.consultaLeadPeloTelefone(tel)).thenReturn(Optional.of(lead));

        doNothing().when(gateway).atualizarContato(eq(10), any());
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
        ContactDto lead = ContactDto.builder()
                .embedded(new ContactDto.Embedded(
                                null, null, List.of(new ContactDto.LeadRef(idLead))
                        )
                ).build();

        // lead encontrado
        when(gateway.consultaLeadPeloTelefone(tel)).thenReturn(Optional.of(lead));

        ContactDto out = useCase.consultaLeadPeloTelefone(tel);
        assertEquals(lead, out);
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

}
