package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
@Order(2)
public class EscolhaPrioritariaRegiao implements EscolhaVendedorType {
    @Override
    public Optional<Vendedor> escolher(Cliente cliente, List<Vendedor> candidatos) {
        return candidatos.stream()
                .filter(v -> v.getRegioes().contains(cliente.getRegiao()))
                .filter(v -> Boolean.TRUE.equals(v.getPrioridade().getPrioritario()))
                .min(Comparator.comparing(v -> v.getPrioridade().getValor()));
    }

}
