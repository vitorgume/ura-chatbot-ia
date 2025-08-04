package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EscolhaVendedorCompositeTest {

    @Test
    void deveRetornarOpcionalDoPrimeiroQueMatch() {
        EscolhaVendedorType a = mock(EscolhaVendedorType.class),
                b = mock(EscolhaVendedorType.class);
        Cliente cli = Cliente.builder().build();
        Vendedor ven = Vendedor.builder().nome("Z").build();
        when(a.escolher(cli, List.of(ven))).thenReturn(Optional.empty());
        when(b.escolher(cli, List.of(ven))).thenReturn(Optional.of(ven));

        EscolhaVendedorComposite comp =
                new EscolhaVendedorComposite(List.of(a, b));
        Optional<Vendedor> res = comp.escolher(cli, List.of(ven));
        assertTrue(res.isPresent());
        assertEquals(ven, res.get());

        InOrder ord = inOrder(a, b);
        ord.verify(a).escolher(cli, List.of(ven));
        ord.verify(b).escolher(cli, List.of(ven));
    }

    @Test
    void deveRetornarEmptySeNenhumMatch() {
        EscolhaVendedorType a = mock(EscolhaVendedorType.class),
                b = mock(EscolhaVendedorType.class);
        Cliente cli = Cliente.builder().build();
        Vendedor ven = Vendedor.builder().nome("Z").build();
        when(a.escolher(cli, List.of(ven))).thenReturn(Optional.empty());
        when(b.escolher(cli, List.of(ven))).thenReturn(Optional.empty());

        EscolhaVendedorComposite comp =
                new EscolhaVendedorComposite(List.of(a, b));
        Optional<Vendedor> res = comp.escolher(cli, List.of(ven));
        assertTrue(res.isEmpty());
        verify(a).escolher(cli, List.of(ven));
        verify(b).escolher(cli, List.of(ven));
    }
}