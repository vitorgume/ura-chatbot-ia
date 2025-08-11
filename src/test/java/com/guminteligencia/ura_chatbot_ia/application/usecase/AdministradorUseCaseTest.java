package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.AdministradorJaExisteException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.AdministradorNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.AdministradorGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Administrador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdministradorUseCaseTest {

    @Mock
    private AdministradorGateway gateway;

    @Mock
    private CriptografiaUseCase criptografiaUseCase;

    @InjectMocks
    private AdministradorUseCase useCase;

    private Administrador novo;
    private final String telefone = "0000000000000";
    private final String senha = "minhaSenha";

    @BeforeEach
    void setup() {
        novo = new Administrador();
        novo.setTelefone(telefone);
        novo.setSenha(senha);
    }

    @Test
    void deveLancarExceptionAoCadastrarSeEmailJaExiste() {
        when(gateway.consultarPorTelefone(telefone))
                .thenReturn(Optional.of(new Administrador()));

        assertThrows(
                AdministradorJaExisteException.class,
                () -> useCase.cadastrar(novo),
                "Quando já existe admin com o mesmo email, deve lançar AdministradorJaExisteException"
        );

        verify(gateway).consultarPorTelefone(telefone);
        verifyNoMoreInteractions(gateway, criptografiaUseCase);
    }

    @Test
    void deveCadastrarComSenhaCriptografadaQuandoEmailNaoExiste() {
        when(gateway.consultarPorTelefone(telefone)).thenReturn(Optional.empty());
        when(criptografiaUseCase.criptografar(senha)).thenReturn("HASHED");
        Administrador salvo = new Administrador();
        salvo.setTelefone(telefone);
        salvo.setSenha("HASHED");
        when(gateway.salvar(any(Administrador.class))).thenReturn(salvo);

        Administrador resultado = useCase.cadastrar(novo);

        assertSame(salvo, resultado);
        ArgumentCaptor<Administrador> cap = ArgumentCaptor.forClass(Administrador.class);
        verify(criptografiaUseCase).criptografar(senha);
        verify(gateway).salvar(cap.capture());
        assertEquals("HASHED", cap.getValue().getSenha());
    }

    @Test
    void deveDeletarSemErroChamaGateway() {
        UUID id = UUID.randomUUID();
        useCase.deletar(id);
        verify(gateway).deletar(id);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void deveRetornarAdministradorQuandoConsultarPorTelefoneEncontrado() {
        Administrador existente = new Administrador();
        existente.setTelefone(telefone);
        when(gateway.consultarPorTelefone(telefone)).thenReturn(Optional.of(existente));

        Administrador res = useCase.consultarPorTelefone(telefone);

        assertSame(existente, res);
    }

    @Test
    void deveLancarAoConsultarPorTelefoneSeNaoEncontrar() {
        when(gateway.consultarPorTelefone(telefone)).thenReturn(Optional.empty());

        assertThrows(
                AdministradorNaoEncontradoException.class,
                () -> useCase.consultarPorTelefone(telefone),
                "Quando não encontrar admin, deve lançar AdministradorNaoEncontradoException"
        );
    }
}