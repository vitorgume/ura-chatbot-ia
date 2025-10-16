package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.domain.MensagemConversa;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.MensagemConversaMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.MensagemConversaRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.MensagemConversaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MensagemConversaDataProviderTest {

    @Mock
    private MensagemConversaRepository repository;

    @InjectMocks
    private MensagemConversaDataProvider provider;

    private final String ERR_LISTAR = "Erro ao listar mensagens da convesa."; // (mantém o texto exatamente como na classe)

    private UUID idConversa;
    private MensagemConversaEntity e1;
    private MensagemConversaEntity e2;
    private MensagemConversa d1;
    private MensagemConversa d2;

    @BeforeEach
    void setup() {
        idConversa = UUID.randomUUID();
        e1 = mock(MensagemConversaEntity.class);
        e2 = mock(MensagemConversaEntity.class);
        d1 = mock(MensagemConversa.class);
        d2 = mock(MensagemConversa.class);
    }

    @Test
    void deveListarPelaConversaComSucesso() {
        when(repository.findByConversaAgente_Id(idConversa))
                .thenReturn(List.of(e1, e2));

        try (MockedStatic<MensagemConversaMapper> ms = mockStatic(MensagemConversaMapper.class)) {
            ms.when(() -> MensagemConversaMapper.paraDomain(e1)).thenReturn(d1);
            ms.when(() -> MensagemConversaMapper.paraDomain(e2)).thenReturn(d2);

            List<MensagemConversa> result = provider.listarPelaConversa(idConversa);

            assertNotNull(result);
            assertEquals(2, result.size());
            assertSame(d1, result.get(0));
            assertSame(d2, result.get(1));

            verify(repository).findByConversaAgente_Id(idConversa);
            ms.verify(() -> MensagemConversaMapper.paraDomain(e1));
            ms.verify(() -> MensagemConversaMapper.paraDomain(e2));
        }
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremMensagens() {
        when(repository.findByConversaAgente_Id(idConversa))
                .thenReturn(List.of());

        try (MockedStatic<MensagemConversaMapper> ms = mockStatic(MensagemConversaMapper.class)) {
            List<MensagemConversa> result = provider.listarPelaConversa(idConversa);

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(repository).findByConversaAgente_Id(idConversa);
            ms.verifyNoInteractions(); // não deve tentar mapear nada
        }
    }

    @Test
    void deveLancarDataProviderExceptionQuandoRepositorioFalhar() {
        when(repository.findByConversaAgente_Id(any(UUID.class)))
                .thenThrow(new RuntimeException("db-fail"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.listarPelaConversa(idConversa)
        );

        assertEquals(ERR_LISTAR, ex.getMessage());

        assertNull(ex.getCause());
        verify(repository).findByConversaAgente_Id(idConversa);
    }

    @Test
    void devePermitirMapperRetornarNull_resultadoContemNull() {
        when(repository.findByConversaAgente_Id(idConversa))
                .thenReturn(List.of(e1));

        try (MockedStatic<MensagemConversaMapper> ms = mockStatic(MensagemConversaMapper.class)) {
            ms.when(() -> MensagemConversaMapper.paraDomain(e1))
                    .thenReturn(null);

            List<MensagemConversa> result = provider.listarPelaConversa(idConversa);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertNull(result.get(0)); // como o código não filtra null, a lista conterá null

            verify(repository).findByConversaAgente_Id(idConversa);
            ms.verify(() -> MensagemConversaMapper.paraDomain(e1));
        }
    }

    @Test
    void devePropagarNullIdParaRepositoryERetornarListaVazia() {
        when(repository.findByConversaAgente_Id(null))
                .thenReturn(List.of());

        List<MensagemConversa> result = provider.listarPelaConversa(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findByConversaAgente_Id(isNull());
    }
}