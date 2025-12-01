package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Order(2)
public class EscolhaPrioritariaRegiao implements EscolhaVendedorType {
    @Override
    public Optional<Vendedor> escolher(Cliente cliente, List<Vendedor> candidatos) {
        if (cliente == null || cliente.getRegiao() == null) return Optional.empty();

        List<Vendedor> elegiveis = candidatos.stream()
                .filter(Objects::nonNull)
                .filter(v -> !Boolean.TRUE.equals(v.getInativo()))
                .filter(v -> VendedorPrioritarioUtil.safe(v.getRegioes())
                        .contains(cliente.getRegiao()))
                .filter(VendedorPrioritarioUtil::isPrioritario)
                .collect(Collectors.toList());

        if (elegiveis.isEmpty()) return Optional.empty();

        Collections.shuffle(elegiveis);

        return elegiveis.stream()
                .min(Comparator.comparingInt(VendedorPrioritarioUtil::prioridadeValor));

    }

}
