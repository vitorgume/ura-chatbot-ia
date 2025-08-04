package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EscolhaAleatoriaTest {

    private final EscolhaAleatoria escolhaAleatoria = new EscolhaAleatoria();

    @Test
    void deveRetornarVazioQuandoListaVazia() {
        Cliente cli = Cliente.builder().build();
        Optional<Vendedor> res = escolhaAleatoria.escolher(cli, List.of());
        assertTrue(res.isEmpty());
    }

    @Test
    void deveRetornarUnicoQuandoApenasUmCandidato() {
        Cliente cli = Cliente.builder().build();
        Vendedor unico = Vendedor.builder().nome("A").build();
        Optional<Vendedor> res = escolhaAleatoria.escolher(cli, List.of(unico));
        assertTrue(res.isPresent());
        assertEquals(unico, res.get());
    }

    @Test
    void deveAlternarEntreDoisCandidatos() throws Exception {
        Random rnd = mock(Random.class);
        when(rnd.nextInt(2)).thenReturn(0, 0, 1);
        Field f = escolhaAleatoria.getClass().getDeclaredField("random");
        f.setAccessible(true);
        f.set(escolhaAleatoria, rnd);

        Cliente cli = Cliente.builder().build();
        Vendedor v0 = Vendedor.builder().nome("X").build();
        Vendedor v1 = Vendedor.builder().nome("Y").build();

        Optional<Vendedor> r1 = escolhaAleatoria.escolher(cli, List.of(v0, v1));
        assertTrue(r1.isPresent());
        String primeiro = r1.get().getNome();
        assertTrue(primeiro.equals("X") || primeiro.equals("Y"));

        Optional<Vendedor> r2 = escolhaAleatoria.escolher(cli, List.of(v0, v1));
        assertTrue(r2.isPresent());
        assertNotEquals(primeiro, r2.get().getNome());
    }
}