package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.VendedorComMesmoTelefoneException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.VendedorNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.VendedorNaoEscolhidoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.VendedorGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendedorUseCaseTest {

    @Mock
    private EscolhaVendedorComposite escolhaComposite;

    @Mock
    private VendedorGateway gateway;

    @InjectMocks
    private VendedorUseCase useCase;

    @BeforeEach
    void resetUltimoVendedor() throws Exception {
        Field f = VendedorUseCase.class.getDeclaredField("ultimoVendedor");
        f.setAccessible(true);
        f.set(null, null);
    }

    @Test
    void cadastrarDeveLancarExceptionQuandoTelefoneJaExiste() {
        Vendedor novo = Vendedor.builder().telefone("+5511999").build();
        Vendedor existente = Vendedor.builder().telefone("+5511999").build();
        when(gateway.consultarPorTelefone("+5511999"))
                .thenReturn(Optional.of(existente));

        assertThrows(
                VendedorComMesmoTelefoneException.class,
                () -> useCase.cadastrar(novo),
                "Telefones iguais devem disparar exceção"
        );

        verify(gateway).consultarPorTelefone("+5511999");
        verify(gateway, never()).salvar(any());
    }

    @Test
    void escolherVendedorDeveRetornarQuandoOptionalPresente() {
        Cliente cli = Cliente.builder().build();
        Vendedor v = Vendedor.builder().nome("X").build();
        when(gateway.listarAtivos()).thenReturn(List.of(v));
        when(escolhaComposite.escolher(cli, List.of(v)))
                .thenReturn(Optional.of(v));

        Vendedor res = useCase.escolherVendedor(cli);
        assertSame(v, res);
    }

    @Test
    void escolherVendedorDeveLancarExceptionQuandoNenhumSelecionado() {
        Cliente cli = Cliente.builder().build();
        when(gateway.listarAtivos()).thenReturn(List.of());
        when(escolhaComposite.escolher(cli, List.of()))
                .thenReturn(Optional.empty());

        assertThrows(
                VendedorNaoEscolhidoException.class,
                () -> useCase.escolherVendedor(cli)
        );
    }

    @Test
    void roletaVendedoresSeSomenteUmRetornaNomeDireto() {
        Vendedor unico = Vendedor.builder().nome("A").inativo(false).build();
        when(gateway.listar()).thenReturn(List.of(unico));

        String nome = useCase.roletaVendedores(null);
        assertEquals("A", nome);
    }

    @Test
    void roletaVendedoresComExcecaoUsaListarComExcecao() {
        when(gateway.listarComExcecao("exc")).thenReturn(List.of(
                Vendedor.builder().nome("B").inativo(false).build()
        ));

        String nome = useCase.roletaVendedores("exc");
        assertEquals("B", nome);
        verify(gateway).listarComExcecao("exc");
    }

    @Test
    void roletaVendedoresEscolheAleatorioMasPulaInativoEAnterior() throws Exception {
        Vendedor inativo = Vendedor.builder().nome("I").inativo(true).build();
        Vendedor v1 = Vendedor.builder().nome("V1").inativo(false).build();
        Vendedor v2 = Vendedor.builder().nome("V2").inativo(false).build();
        List<Vendedor> lista = List.of(inativo, v1, v2);
        when(gateway.listar()).thenReturn(lista);

        Random rnd = mock(Random.class);
        when(rnd.nextInt(lista.size())).thenReturn(0, 1);

        Field rf = VendedorUseCase.class.getDeclaredField("random");
        rf.setAccessible(true);
        rf.set(useCase, rnd);

        String escolhido = useCase.roletaVendedores(null);
        assertEquals("V1", escolhido);
    }

    @Test
    void consultarVendedorRetornaQuandoEncontrado() {
        Vendedor v = Vendedor.builder().nome("Z").build();
        when(gateway.consultarVendedor("Z")).thenReturn(Optional.of(v));

        Vendedor res = useCase.consultarVendedor("Z");
        assertSame(v, res);
    }

    @Test
    void consultarVendedorLancaExceptionQuandoNaoEncontrado() {
        when(gateway.consultarVendedor("N")).thenReturn(Optional.empty());
        assertThrows(
                VendedorNaoEncontradoException.class,
                () -> useCase.consultarVendedor("N")
        );
    }

    @Test
    void roletaVendedoresConversaInativaSeSegmentoNaoNuloChamaEscolher() {
        Cliente cli = Cliente.builder().build();
        cli.setSegmento(null);

        cli.setSegmento(null);
        Cliente cli2 = Cliente.builder().build();
        cli2.setSegmento(Segmento.MEDICINA_SAUDE);
        Vendedor v = Vendedor.builder().nome("X").build();
        when(escolhaComposite.escolher(eq(cli2), anyList()))
                .thenReturn(Optional.of(v));

        Vendedor res = useCase.roletaVendedoresConversaInativa(cli2);
        assertSame(v, res);
    }

    @Test
    void roletaVendedoresConversaInativaSeSegmentoNuloChamaRoletaEConsultar() {
        Cliente cli = Cliente.builder().build();
        cli.setSegmento(null);

        VendedorUseCase spyUC = spy(useCase);
        doReturn("NM").when(spyUC).roletaVendedores("Nilza");
        Vendedor v = Vendedor.builder().nome("NM").build();
        when(gateway.consultarVendedor("NM")).thenReturn(Optional.of(v));

        Vendedor res = spyUC.roletaVendedoresConversaInativa(cli);
        assertSame(v, res);
    }

    @Test
    void alterarDeveChamarSalvarERetornar() {
        Vendedor orig = Vendedor.builder().id(1L).build();
        Vendedor novos = Vendedor.builder().id(1L).nome("Novo").build();
        when(gateway.consultarPorId(1L)).thenReturn(Optional.of(orig));
        when(gateway.salvar(orig)).thenReturn(novos);

        Vendedor res = useCase.alterar(novos, 1L);
        assertEquals("Novo", res.getNome());
        verify(gateway).consultarPorId(1L);
        verify(gateway).salvar(orig);
    }

    @Test
    void alterarDeveLancarExceptionQuandoNaoEncontrarPorId() {
        when(gateway.consultarPorId(2L)).thenReturn(Optional.empty());
        assertThrows(
                VendedorNaoEncontradoException.class,
                () -> useCase.alterar(Vendedor.builder().build(), 2L)
        );
    }

    @Test
    void listarDeveDelegarParaGateway() {
        List<Vendedor> lista = List.of(Vendedor.builder().nome("A").build());
        when(gateway.listar()).thenReturn(lista);
        List<Vendedor> res = useCase.listar();
        assertSame(lista, res);
    }

    @Test
    void deletarDeveChamarConsultarEDeletar() {
        when(gateway.consultarPorId(3L))
                .thenReturn(Optional.of(Vendedor.builder().id(3L).build()));
        useCase.deletar(3L);
        verify(gateway).consultarPorId(3L);
        verify(gateway).deletar(3L);
    }

    @Test
    void deletarDeveLancarExceptionQuandoNaoEncontrar() {
        when(gateway.consultarPorId(4L)).thenReturn(Optional.empty());
        assertThrows(
                VendedorNaoEncontradoException.class,
                () -> useCase.deletar(4L)
        );
    }
}