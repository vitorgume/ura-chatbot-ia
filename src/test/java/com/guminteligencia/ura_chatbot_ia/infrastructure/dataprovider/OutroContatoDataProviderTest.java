package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.OutroContatoMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.OutroContatoRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.OutroContatoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutroContatoDataProviderTest {

    @Mock
    private OutroContatoRepository repository;

    @InjectMocks
    private OutroContatoDataProvider provider;

    private final String ERR_MSG = "Erro ao consultar por nome outro contato.";
    private final String nome = "Teste";

    private OutroContatoEntity entity;
    private OutroContato domain;

    @BeforeEach
    void setup() {
        entity = mock(OutroContatoEntity.class);
        domain = mock(OutroContato.class);
    }

    @Test
    void deveConsultarPorNomeComSucesso() {
        when(repository.findByNome(nome)).thenReturn(Optional.of(entity));

        try (MockedStatic<OutroContatoMapper> ms = mockStatic(OutroContatoMapper.class)) {
            ms.when(() -> OutroContatoMapper.paraDomain(entity)).thenReturn(domain);

            Optional<OutroContato> result = provider.consultarPorNome(nome);

            assertTrue(result.isPresent());
            assertSame(domain, result.get());

            verify(repository).findByNome(nome);
            ms.verify(() -> OutroContatoMapper.paraDomain(entity));
        }
    }

    @Test
    void deveRetornarVazioAoConsultarPorNome() {
        when(repository.findByNome(nome)).thenReturn(Optional.empty());

        Optional<OutroContato> result = provider.consultarPorNome(nome);

        assertTrue(result.isEmpty());
        verify(repository).findByNome(nome);
    }

    @Test
    void deveLancarExceptionAoConsultarPorNome() {
        when(repository.findByNome(anyString()))
                .thenThrow(new RuntimeException("fail-query"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.consultarPorNome(nome)
        );

        assertEquals(ERR_MSG, ex.getMessage());
        verify(repository).findByNome(nome);
    }

}