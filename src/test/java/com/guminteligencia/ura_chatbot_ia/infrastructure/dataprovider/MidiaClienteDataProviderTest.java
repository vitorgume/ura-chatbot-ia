package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.domain.MidiaCliente;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.MidiaClienteMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.MidiaClienteRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.MidiaClienteEntity;
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
class MidiaClienteDataProviderTest {

    @Mock
    private MidiaClienteRepository repository;

    @InjectMocks
    private MidiaClienteDataProvider provider;

    private final String ERR_TEL = "Erro ao consultar midia de cliente pelo seu telefone.";

    private MidiaClienteEntity entity;
    private MidiaCliente domain;
    private String telefone;

    @BeforeEach
    void setup() {
        entity = mock(MidiaClienteEntity.class);
        domain = mock(MidiaCliente.class);
        telefone = "+554499999999";
    }

    @Test
    void deveConsultarMidiaPorTelefoneComSucesso() {
        when(repository.findByTelefoneClienteFetch(telefone))
                .thenReturn(Optional.of(entity));

        try (MockedStatic<MidiaClienteMapper> ms = mockStatic(MidiaClienteMapper.class)) {
            ms.when(() -> MidiaClienteMapper.paraDomain(entity))
                    .thenReturn(domain);

            Optional<MidiaCliente> result = provider.consultarMidiaPeloTelefoneCliente(telefone);

            assertTrue(result.isPresent());
            assertSame(domain, result.get());

            verify(repository).findByTelefoneClienteFetch(telefone);
            ms.verify(() -> MidiaClienteMapper.paraDomain(entity));
        }
    }

    @Test
    void deveRetornarVazioQuandoNaoEncontrarMidia() {
        when(repository.findByTelefoneClienteFetch(telefone))
                .thenReturn(Optional.empty());

        Optional<MidiaCliente> result = provider.consultarMidiaPeloTelefoneCliente(telefone);

        assertTrue(result.isEmpty());
        verify(repository).findByTelefoneClienteFetch(telefone);
    }

    @Test
    void deveRetornarVazioQuandoMapperRetornarNull() {
        when(repository.findByTelefoneClienteFetch(telefone))
                .thenReturn(Optional.of(entity));

        try (MockedStatic<MidiaClienteMapper> ms = mockStatic(MidiaClienteMapper.class)) {
            // se o mapper retornar null, Optional.map -> Optional.empty()
            ms.when(() -> MidiaClienteMapper.paraDomain(entity))
                    .thenReturn(null);

            Optional<MidiaCliente> result = provider.consultarMidiaPeloTelefoneCliente(telefone);

            assertTrue(result.isEmpty());
            verify(repository).findByTelefoneClienteFetch(telefone);
            ms.verify(() -> MidiaClienteMapper.paraDomain(entity));
        }
    }

    @Test
    void deveLancarExceptionAoConsultarMidiaPorTelefone() {
        when(repository.findByTelefoneClienteFetch(anyString()))
                .thenThrow(new RuntimeException("db-down"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.consultarMidiaPeloTelefoneCliente(telefone)
        );

        assertEquals(ERR_TEL, ex.getMessage());
        // o provider propaga ex.getCause(); para RuntimeException simples, a cause é null:
        assertNull(ex.getCause());
        verify(repository).findByTelefoneClienteFetch(telefone);
    }

    @Test
    void devePropagarNullTelefoneParaRepository() {
        when(repository.findByTelefoneClienteFetch(null))
                .thenReturn(Optional.empty());

        Optional<MidiaCliente> result = provider.consultarMidiaPeloTelefoneCliente(null);

        assertTrue(result.isEmpty());
        verify(repository).findByTelefoneClienteFetch((String) isNull());
    }

}