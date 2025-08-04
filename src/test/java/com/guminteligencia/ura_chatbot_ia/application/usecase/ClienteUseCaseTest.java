package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.ClienteNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.ClienteGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.RelatorioContatoDto;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteUseCaseTest {

    @Mock
    private ClienteGateway gateway;

    @InjectMocks
    private ClienteUseCase useCase;

    private final String telefone = "+5511999000111";
    private UUID idCliente;
    private Cliente clienteSalvo;

    @BeforeEach
    void setup() {
        idCliente = UUID.randomUUID();
        clienteSalvo = Cliente.builder()
                .id(idCliente)
                .telefone(telefone)
                .build();
    }

    @Test
    void deveDelegarConsultarPorTelefone() {
        when(gateway.consultarPorTelefone(telefone))
                .thenReturn(Optional.of(clienteSalvo));

        Optional<Cliente> resultado = useCase.consultarPorTelefone(telefone);

        assertTrue(resultado.isPresent());
        assertSame(clienteSalvo, resultado.get());
        verify(gateway).consultarPorTelefone(telefone);
    }

    @Test
    void deveCadastrarClienteComTelefone() {
        when(gateway.salvar(any(Cliente.class))).thenReturn(clienteSalvo);

        Cliente resultado = useCase.cadastrar(telefone);

        assertSame(clienteSalvo, resultado);
        ArgumentCaptor<Cliente> cap = ArgumentCaptor.forClass(Cliente.class);
        verify(gateway).salvar(cap.capture());
        assertEquals(telefone, cap.getValue().getTelefone());
    }

    @Test
    void deveAlterarClienteQuandoEncontrado() {
        Cliente novosDados = Cliente.builder()
                .nome("NovoNome")
                .telefone("+552211223344")
                .build();

        Cliente existente = spy(Cliente.builder()
                .id(idCliente)
                .telefone(telefone)
                .build());
        when(gateway.consultarPorId(idCliente))
                .thenReturn(Optional.of(existente));
        when(gateway.salvar(existente)).thenReturn(existente);

        Cliente resultado = useCase.alterar(novosDados, idCliente);

        assertSame(existente, resultado);
        verify(existente).setDados(novosDados);
        verify(gateway).salvar(existente);
    }

    @Test
    void deveLancarQuandoAlterarQuandoNaoEncontrado() {
        when(gateway.consultarPorId(idCliente))
                .thenReturn(Optional.empty());

        assertThrows(
                ClienteNaoEncontradoException.class,
                () -> useCase.alterar(Cliente.builder().build(), idCliente)
        );
        verify(gateway).consultarPorId(idCliente);
        verify(gateway, never()).salvar(any());
    }

    @Test
    void deveRetornarRelatorioSegundaFeiraDelegandoAoGateway() {
        List<RelatorioContatoDto> rel = List.of(mock(RelatorioContatoDto.class));
        when(gateway.getRelatorioContato()).thenReturn(rel);

        List<RelatorioContatoDto> resultado = useCase.getRelatorioSegundaFeira();

        assertSame(rel, resultado);
        verify(gateway).getRelatorioContato();
    }

    @Test
    void deveRetornarRelatorioDelegandoAoGateway() {
        List<RelatorioContatoDto> rel = List.of(mock(RelatorioContatoDto.class));
        when(gateway.getRelatorioContatoSegundaFeira()).thenReturn(rel);

        List<RelatorioContatoDto> resultado = useCase.getRelatorio();

        assertSame(rel, resultado);
        verify(gateway).getRelatorioContatoSegundaFeira();
    }

}