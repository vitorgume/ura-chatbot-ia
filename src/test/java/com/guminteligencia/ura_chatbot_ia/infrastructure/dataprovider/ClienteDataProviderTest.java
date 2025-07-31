package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.RelatorioContatoDto;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.ClienteMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.RelatorioMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.ClienteRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ClienteEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteDataProviderTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteDataProvider provider;

    private final String ERR_TEL = "Erro ao consultar cliente pelo seu telefone.";
    private final String ERR_SALVAR = "Erro ao salvar cliente.";
    private final String ERR_ID = "Erro ao consultar cliente pelo seu id.";
    private final String ERR_REL = "Erro ao gerar relatório de contatos.";
    private final String ERR_REL2 = "Erro ao gerar relatório de segunda feira.";

    private ClienteEntity entityIn;
    private ClienteEntity entityOut;
    private Cliente domainIn;
    private Cliente domainOut;
    private UUID id;
    private String telefone;

    @BeforeEach
    void setup() {
        entityIn = mock(ClienteEntity.class);
        entityOut = mock(ClienteEntity.class);
        domainIn = mock(Cliente.class);
        domainOut = mock(Cliente.class);
        id = UUID.randomUUID();
        telefone = "+5511999999999";
    }

    @Test
    void deveConsultarPorTelefoneComSucesso() {
        when(repository.findByTelefone(telefone))
                .thenReturn(Optional.of(entityIn));

        try (MockedStatic<ClienteMapper> ms = mockStatic(ClienteMapper.class)) {
            ms.when(() -> ClienteMapper.paraDomain(entityIn))
                    .thenReturn(domainOut);

            Optional<Cliente> result = provider.consultarPorTelefone(telefone);
            assertTrue(result.isPresent());
            assertSame(domainOut, result.get());

            verify(repository).findByTelefone(telefone);
            ms.verify(() -> ClienteMapper.paraDomain(entityIn));
        }
    }

    @Test
    void deveRetornaVazioAoConsultarPorTelefone() {
        when(repository.findByTelefone(telefone))
                .thenReturn(Optional.empty());

        Optional<Cliente> result = provider.consultarPorTelefone(telefone);
        assertTrue(result.isEmpty());
        verify(repository).findByTelefone(telefone);
    }

    @Test
    void deveLancarExceptionAoConsultarPorTelefone() {
        when(repository.findByTelefone(anyString()))
                .thenThrow(new RuntimeException("fail-tel"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.consultarPorTelefone(telefone)
        );
        assertEquals(ERR_TEL, ex.getMessage());
    }

    @Test
    void deveSalvarComSucesso() {
        try (MockedStatic<ClienteMapper> ms = mockStatic(ClienteMapper.class)) {
            ms.when(() -> ClienteMapper.paraEntity(domainIn))
                    .thenReturn(entityIn);
            when(repository.save(entityIn))
                    .thenReturn(entityOut);
            ms.when(() -> ClienteMapper.paraDomain(entityOut))
                    .thenReturn(domainOut);

            Cliente result = provider.salvar(domainIn);
            assertSame(domainOut, result);

            verify(repository).save(entityIn);
            ms.verify(() -> ClienteMapper.paraEntity(domainIn));
            ms.verify(() -> ClienteMapper.paraDomain(entityOut));
        }
    }

    @Test
    void deveLancarExceptionAoSalvar() {
        try (MockedStatic<ClienteMapper> ms = mockStatic(ClienteMapper.class)) {
            ms.when(() -> ClienteMapper.paraEntity(domainIn))
                    .thenReturn(entityIn);
            when(repository.save(entityIn))
                    .thenThrow(new RuntimeException("fail-save"));

            DataProviderException ex = assertThrows(
                    DataProviderException.class,
                    () -> provider.salvar(domainIn)
            );
            assertEquals(ERR_SALVAR, ex.getMessage());
        }
    }

    @Test
    void deveConsultarPorIdComSucesso() {
        when(repository.findById(id))
                .thenReturn(Optional.of(entityIn));

        try (MockedStatic<ClienteMapper> ms = mockStatic(ClienteMapper.class)) {
            ms.when(() -> ClienteMapper.paraDomain(entityIn))
                    .thenReturn(domainOut);

            Optional<Cliente> result = provider.consultarPorId(id);
            assertTrue(result.isPresent());
            assertSame(domainOut, result.get());

            verify(repository).findById(id);
            ms.verify(() -> ClienteMapper.paraDomain(entityIn));
        }
    }

    @Test
    void deveRetornarVazioComSucessoAoConsultarPorId() {
        when(repository.findById(id))
                .thenReturn(Optional.empty());

        Optional<Cliente> result = provider.consultarPorId(id);
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
    void deveRetornarRelatoriosDeContatosComSucesso() {
        List<Object[]> raw = Collections.singletonList(new Object[0]);
        List<RelatorioContatoDto> dtoList = List.of(mock(RelatorioContatoDto.class));

        when(repository.gerarRelatorio()).thenReturn(raw);
        try (MockedStatic<RelatorioMapper> ms = mockStatic(RelatorioMapper.class)) {
            ms.when(() -> RelatorioMapper.paraDto(raw))
                    .thenReturn(dtoList);

            List<RelatorioContatoDto> result = provider.getRelatorioContato();
            assertSame(dtoList, result);

            verify(repository).gerarRelatorio();
            ms.verify(() -> RelatorioMapper.paraDto(raw));
        }
    }

    @Test
    void deveLancarExceptionAoRetornarRelatorioDeContatos() {
        when(repository.gerarRelatorio())
                .thenThrow(new RuntimeException("fail-rel"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.getRelatorioContato()
        );
        assertEquals(ERR_REL, ex.getMessage());
    }


    @Test
    void deveRetornaRelatorioDeContatosDeSegundaFeira() {
        List<Object[]> raw = Collections.singletonList(new Object[0]);
        List<RelatorioContatoDto> dtoList = List.of(mock(RelatorioContatoDto.class));

        when(repository.gerarRelatorioSegundaFeira()).thenReturn(raw);
        try (MockedStatic<RelatorioMapper> ms = mockStatic(RelatorioMapper.class)) {
            ms.when(() -> RelatorioMapper.paraDto(raw))
                    .thenReturn(dtoList);

            List<RelatorioContatoDto> result = provider.getRelatorioContatoSegundaFeira();
            assertSame(dtoList, result);

            verify(repository).gerarRelatorioSegundaFeira();
            ms.verify(() -> RelatorioMapper.paraDto(raw));
        }
    }

    @Test
    void deveLancarExceptionAoRetornarRelatorioDeContatosDeSegundaFeira() {
        when(repository.gerarRelatorioSegundaFeira())
                .thenThrow(new RuntimeException("fail-rel2"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.getRelatorioContatoSegundaFeira()
        );
        assertEquals(ERR_REL2, ex.getMessage());
    }
}