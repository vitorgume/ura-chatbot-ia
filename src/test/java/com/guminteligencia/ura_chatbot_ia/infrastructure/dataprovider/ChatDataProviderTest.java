package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.domain.Chat;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.ChatMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.ChatRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ChatEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatDataProviderTest {

    @Mock
    private ChatRepository repository;

    @InjectMocks
    private ChatDataProvider provider;

    private final String ERR_ID = "Erro ao consultar chat pelo seu id.";
    private final String ERR_SALVAR = "Erro ao salvar chat.";

    private ChatEntity entityIn;
    private ChatEntity entityOut;
    private Chat domainIn;
    private Chat domainOut;
    private UUID id;

    @BeforeEach
    void setup() {
        entityIn  = mock(ChatEntity.class);
        entityOut = mock(ChatEntity.class);
        domainIn  = mock(Chat.class);
        domainOut = mock(Chat.class);
        id = UUID.randomUUID();
    }

    // ====== consultarPorId ======

    @Test
    void deveConsultarPorIdComSucesso() {
        when(repository.findById(id)).thenReturn(Optional.of(entityIn));

        try (MockedStatic<ChatMapper> ms = mockStatic(ChatMapper.class)) {
            ms.when(() -> ChatMapper.paraDomain(entityIn))
                    .thenReturn(domainOut);

            Optional<Chat> result = provider.consultarPorId(id);

            assertTrue(result.isPresent());
            assertSame(domainOut, result.get());

            verify(repository).findById(id);
            ms.verify(() -> ChatMapper.paraDomain(entityIn));
        }
    }

    @Test
    void deveRetornarVazioAoConsultarPorId() {
        when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Chat> result = provider.consultarPorId(id);

        assertTrue(result.isEmpty());
        verify(repository).findById(id);
    }

    @Test
    void deveLancarExceptionAoConsultarPorIdPropagandoCause() {
        var cause = new SQLException("db down");
        var boom  = new RuntimeException("repo error", cause);

        when(repository.findById(any(UUID.class))).thenThrow(boom);

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.consultarPorId(id)
        );
        assertEquals(ERR_ID, ex.getMessage());
        assertSame(cause, ex.getCause()); // provider usa ex.getCause()
        verify(repository).findById(id);
    }

    // ====== salvar ======

    @Test
    void deveSalvarComSucesso() {
        try (MockedStatic<ChatMapper> ms = mockStatic(ChatMapper.class)) {
            ms.when(() -> ChatMapper.paraEntity(domainIn)).thenReturn(entityIn);
            when(repository.save(entityIn)).thenReturn(entityOut);
            ms.when(() -> ChatMapper.paraDomain(entityOut)).thenReturn(domainOut);

            Chat result = provider.salvar(domainIn);

            assertSame(domainOut, result);

            verify(repository).save(entityIn);
            ms.verify(() -> ChatMapper.paraEntity(domainIn));
            ms.verify(() -> ChatMapper.paraDomain(entityOut));
        }
    }

    @Test
    void deveLancarExceptionAoSalvarPropagandoCause() {
        var cause = new SQLException("write fail");
        var boom  = new RuntimeException("repo error", cause);

        try (MockedStatic<ChatMapper> ms = mockStatic(ChatMapper.class)) {
            ms.when(() -> ChatMapper.paraEntity(domainIn)).thenReturn(entityIn);
            when(repository.save(entityIn)).thenThrow(boom);

            DataProviderException ex = assertThrows(
                    DataProviderException.class,
                    () -> provider.salvar(domainIn)
            );
            assertEquals(ERR_SALVAR, ex.getMessage());
            assertSame(cause, ex.getCause());
            verify(repository).save(entityIn);
        }
    }
}