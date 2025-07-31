package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.ContextoMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.ContextoRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ContextoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContextoDataProviderTest {

    @Mock
    private ContextoRepository repository;

    @InjectMocks
    private ContextoDataProvider provider;

    private Contexto domainIn;
    private Contexto domainOut;
    private ContextoEntity entity;

    private final String ERR_DELETE = "Erro ao deletar contexto do banco de dados.";
    private final String ERR_FIND = "Erro ao consultar contexto pelo seu id.";

    private UUID id;

    @BeforeEach
    void setup() {
        domainIn = mock(Contexto.class);
        domainOut = mock(Contexto.class);
        entity = mock(ContextoEntity.class);
        id = UUID.randomUUID();
    }

    @Test
    void deveDeletarComSucesso() {
        try (MockedStatic<ContextoMapper> ms = mockStatic(ContextoMapper.class)) {
            ms.when(() -> ContextoMapper.paraEntity(domainIn)).thenReturn(entity);

            provider.deletar(domainIn);

            verify(repository).deletar(entity);
            ms.verify(() -> ContextoMapper.paraEntity(domainIn));
        }
    }

    @Test
    void deveLancarExceptionAoDeletar() {
        try (MockedStatic<ContextoMapper> ms = mockStatic(ContextoMapper.class)) {
            ms.when(() -> ContextoMapper.paraEntity(domainIn)).thenReturn(entity);
            doThrow(new RuntimeException("fail-del"))
                    .when(repository).deletar(entity);

            DataProviderException ex = assertThrows(
                    DataProviderException.class,
                    () -> provider.deletar(domainIn)
            );
            assertEquals(ERR_DELETE, ex.getMessage());
        }
    }


    @Test
    void deveConsultarPorIdComSucesso() {
        when(repository.consultarPorId(id))
                .thenReturn(Optional.of(entity));

        try (MockedStatic<ContextoMapper> ms = mockStatic(ContextoMapper.class)) {
            ms.when(() -> ContextoMapper.paraDomain(entity)).thenReturn(domainOut);

            Optional<Contexto> result = provider.consultarPorId(id);
            assertTrue(result.isPresent());
            assertSame(domainOut, result.get());

            verify(repository).consultarPorId(id);
            ms.verify(() -> ContextoMapper.paraDomain(entity));
        }
    }

    @Test
    void deveRetornarVazioComSucessoAoConsultarPorId() {
        when(repository.consultarPorId(id))
                .thenReturn(Optional.empty());

        Optional<Contexto> result = provider.consultarPorId(id);
        assertTrue(result.isEmpty());
        verify(repository).consultarPorId(id);
    }

    @Test
    void deveLancarExceptionAoConsultarPorId() {
        when(repository.consultarPorId(any(UUID.class)))
                .thenThrow(new RuntimeException("fail-find"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.consultarPorId(id)
        );
        assertEquals(ERR_FIND, ex.getMessage());
    }

}