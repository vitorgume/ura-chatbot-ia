package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.domain.Administrador;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.AdministradorMappper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.AdministradorRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.AdministradorEntity;
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
class AdministradorDataProviderTest {

    @Mock
    private AdministradorRepository repository;

    @InjectMocks
    private AdministradorDataProvider provider;

    private final String ERR_CONSULT =
            "Erro ao consultar administrador pelo seu email.";
    private final String ERR_SALVAR =
            "Erro ao salvar novo administrador.";
    private final String ERR_DELETAR =
            "Erro ao deletar administrador.";

    private AdministradorEntity entityIn;
    private AdministradorEntity entityOut;
    private Administrador domainIn;
    private Administrador domainOut;

    @BeforeEach
    void setup() {
        entityIn  = mock(AdministradorEntity.class);
        entityOut = mock(AdministradorEntity.class);
        domainIn  = mock(Administrador.class);
        domainOut = mock(Administrador.class);
    }

    @Test
    void deveConsultarPorEmailComSucesso() {
        when(repository.findByEmail("foo@x.com"))
                .thenReturn(Optional.of(entityIn));

        try (MockedStatic<AdministradorMappper> ms =
                     mockStatic(AdministradorMappper.class)) {
            ms.when(() -> AdministradorMappper.paraDomain(entityIn))
                    .thenReturn(domainOut);

            Optional<Administrador> result = provider.consultarPorEmail("foo@x.com");
            assertTrue(result.isPresent());
            assertSame(domainOut, result.get());

            verify(repository).findByEmail("foo@x.com");
            ms.verify(() -> AdministradorMappper.paraDomain(entityIn));
        }
    }

    @Test
    void deveRetornarAdministradorVazioAoConsultarPorEmail() {
        when(repository.findByEmail("nope@x.com"))
                .thenReturn(Optional.empty());

        Optional<Administrador> result = provider.consultarPorEmail("nope@x.com");
        assertTrue(result.isEmpty());
        verify(repository).findByEmail("nope@x.com");
    }

    @Test
    void deveLancarExceptinAoConsultarPorEmail() {
        when(repository.findByEmail(anyString()))
                .thenThrow(new RuntimeException("fail-consulta"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.consultarPorEmail("e@mail.com")
        );
        assertEquals(ERR_CONSULT, ex.getMessage());
    }

    @Test
    void deveSalvarComSucesso() {
        try (MockedStatic<AdministradorMappper> ms =
                     mockStatic(AdministradorMappper.class)) {
            ms.when(() -> AdministradorMappper.paraEntity(domainIn))
                    .thenReturn(entityIn);
            when(repository.save(entityIn)).thenReturn(entityOut);
            ms.when(() -> AdministradorMappper.paraDomain(entityOut))
                    .thenReturn(domainOut);

            Administrador result = provider.salvar(domainIn);
            assertSame(domainOut, result);

            verify(repository).save(entityIn);
            ms.verify(() -> AdministradorMappper.paraEntity(domainIn));
            ms.verify(() -> AdministradorMappper.paraDomain(entityOut));
        }
    }

    @Test
    void deveLancarExceptionAoSalvar() {
        try (MockedStatic<AdministradorMappper> ms =
                     mockStatic(AdministradorMappper.class)) {
            ms.when(() -> AdministradorMappper.paraEntity(domainIn))
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
    void deletar_success() {
        UUID id = UUID.randomUUID();
        // sem exceção
        provider.deletar(id);
        verify(repository).deleteById(id);
    }

    @Test
    void deletar_repositoryThrows_throwsDataProviderException() {
        UUID id = UUID.randomUUID();
        doThrow(new RuntimeException("fail-del"))
                .when(repository).deleteById(id);

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.deletar(id)
        );
        assertEquals(ERR_DELETAR, ex.getMessage());
        assertEquals("fail-del", ex.getCause().getMessage());
    }
}