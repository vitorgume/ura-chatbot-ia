package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
@Order(3)
public class EscolhaAleatoria implements EscolhaVendedorType {
    private final Random random = new Random();
    private String ultimo;

    @Override
    public Optional<Vendedor> escolher(Cliente cliente, List<Vendedor> candidatos) {
        if (candidatos.isEmpty()) return Optional.empty();
        if (candidatos.size() == 1) return Optional.of(candidatos.get(0));

        Vendedor escolhido;
        do {
            escolhido = candidatos.get(random.nextInt(candidatos.size()));
        } while (candidatos.size() > 1 && escolhido.getNome().equals(ultimo));
        ultimo = escolhido.getNome();
        return Optional.of(escolhido);
    }
}
