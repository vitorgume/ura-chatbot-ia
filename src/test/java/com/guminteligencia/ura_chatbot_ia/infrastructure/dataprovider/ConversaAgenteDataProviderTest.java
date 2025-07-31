package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.ConversaAgenteMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.ConversaAgenteRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConversaAgenteEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversaAgenteDataProviderTest {

    @Mock
    private ConversaAgenteRepository repository;

    @InjectMocks
    private ConversaAgenteDataProvider provider;

    private final String ERR_SAVE = "Erro ao salvar conversa do agente.";
    private final String ERR_ID_CLI = "Erro ao consultar conversa pelo id do cliente.";
    private final String ERR_ID = "Erro ao consultar conversa pelo seu id.";
    private final String ERR_LIST = "Erro ao listar conversas não finalizadas.";

    private ConversaAgente domainIn;
    private ConversaAgente domainOut;
    private ConversaAgenteEntity entityIn;
    private ConversaAgenteEntity entityOut;
    private UUID id;

    @BeforeEach
    void setup() {
        domainIn = mock(ConversaAgente.class);
        domainOut = mock(ConversaAgente.class);
        entityIn = mock(ConversaAgenteEntity.class);
        entityOut = mock(ConversaAgenteEntity.class);
        id = UUID.randomUUID();
    }

    @Test
    void deveSalvarComSucesso() {
        try (MockedStatic<ConversaAgenteMapper> ms = mockStatic(ConversaAgenteMapper.class)) {
            ms.when(() -> ConversaAgenteMapper.paraEntity(domainIn))
                    .thenReturn(entityIn);
            when(repository.save(entityIn)).thenReturn(entityOut);
            ms.when(() -> ConversaAgenteMapper.paraDomain(entityOut))
                    .thenReturn(domainOut);

            ConversaAgente result = provider.salvar(domainIn);

            assertSame(domainOut, result);
            verify(repository).save(entityIn);
            ms.verify(() -> ConversaAgenteMapper.paraEntity(domainIn));
            ms.verify(() -> ConversaAgenteMapper.paraDomain(entityOut));
        }
    }

    @Test
    void deveLancarExceptionAoSalvar() {
        try (MockedStatic<ConversaAgenteMapper> ms = mockStatic(ConversaAgenteMapper.class)) {
            ms.when(() -> ConversaAgenteMapper.paraEntity(domainIn))
                    .thenReturn(entityIn);
            when(repository.save(entityIn))
                    .thenThrow(new RuntimeException("fail-save"));

            DataProviderException ex = assertThrows(
                    DataProviderException.class,
                    () -> provider.salvar(domainIn)
            );
            assertEquals(ERR_SAVE, ex.getMessage());
        }
    }

    @Test
    void deveConsultarPorClienteComSucesso() {
        when(repository.findByCliente_Id(id))
                .thenReturn(Optional.of(entityIn));

        try (MockedStatic<ConversaAgenteMapper> ms = mockStatic(ConversaAgenteMapper.class)) {
            ms.when(() -> ConversaAgenteMapper.paraDomain(entityIn))
                    .thenReturn(domainOut);

            Optional<ConversaAgente> result = provider.consultarPorIdCliente(id);
            assertTrue(result.isPresent());
            assertSame(domainOut, result.get());

            verify(repository).findByCliente_Id(id);
            ms.verify(() -> ConversaAgenteMapper.paraDomain(entityIn));
        }
    }

    @Test
    void deveRetornarVazioAoConsultarPorCliente() {
        when(repository.findByCliente_Id(id))
                .thenReturn(Optional.empty());

        Optional<ConversaAgente> result = provider.consultarPorIdCliente(id);
        assertTrue(result.isEmpty());
        verify(repository).findByCliente_Id(id);
    }

    @Test
    void deveLancarexceptionAoConsultarPorCliente() {
        when(repository.findByCliente_Id(any(UUID.class)))
                .thenThrow(new RuntimeException("fail-id-cli"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.consultarPorIdCliente(id)
        );
        assertEquals(ERR_ID_CLI, ex.getMessage());
    }

    @Test
    void deveConsultarPorIdComSucesso() {
        when(repository.findById(id))
                .thenReturn(Optional.of(entityIn));

        try (MockedStatic<ConversaAgenteMapper> ms = mockStatic(ConversaAgenteMapper.class)) {
            ms.when(() -> ConversaAgenteMapper.paraDomain(entityIn))
                    .thenReturn(domainOut);

            Optional<ConversaAgente> result = provider.consultarPorId(id);
            assertTrue(result.isPresent());
            assertSame(domainOut, result.get());

            verify(repository).findById(id);
            ms.verify(() -> ConversaAgenteMapper.paraDomain(entityIn));
        }
    }

    @Test
    void deveRetornarVazioAoConsultarPorId() {
        when(repository.findById(id))
                .thenReturn(Optional.empty());

        Optional<ConversaAgente> result = provider.consultarPorId(id);
        assertTrue(result.isEmpty());
        verify(repository).findById(id);
    }

    @Test
    void deveLancarExceptionAoConsultarPorId() {
        when(repository.findById(any(UUID.class)))
                .thenThrow(new RuntimeException("fail-id"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.consultarPorId(id)
        );
        assertEquals(ERR_ID, ex.getMessage());
    }

    @Test
    void deveListarConversasNaoFinalizadasComSucesso() {
        List<ConversaAgenteEntity> raw = List.of(entityIn);
        List<ConversaAgente> expected = List.of(domainOut);

        when(repository.listarNaoFinalizadas()).thenReturn(raw);
        try (MockedStatic<ConversaAgenteMapper> ms = mockStatic(ConversaAgenteMapper.class)) {
            ms.when(() -> ConversaAgenteMapper.paraDomain(entityIn))
                    .thenReturn(domainOut);

            List<ConversaAgente> result = provider.listarNaoFinalizados();
            assertEquals(expected, result);
            verify(repository).listarNaoFinalizadas();
            ms.verify(() -> ConversaAgenteMapper.paraDomain(entityIn));
        }
    }

    @Test
    void deveLancarExceptionAoListarConversasNaoFinalizadas() {
        when(repository.listarNaoFinalizadas())
                .thenThrow(new RuntimeException("fail-list"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.listarNaoFinalizados()
        );
        assertEquals(ERR_LIST, ex.getMessage());
    }

}