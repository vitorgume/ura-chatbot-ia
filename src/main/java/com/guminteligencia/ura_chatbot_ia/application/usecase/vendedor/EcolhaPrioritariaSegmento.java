package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import ch.qos.logback.core.net.server.Client;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Order(1)
public class EcolhaPrioritariaSegmento implements EscolhaVendedorType {

    @Override
    public Optional<Vendedor> escolher(Cliente cliente, List<Vendedor> candidatos) {
        if (cliente == null || cliente.getSegmento() == null) return Optional.empty();

        List<Vendedor> elegiveis = candidatos.stream()
                .filter(Objects::nonNull)
                .filter(v -> !Boolean.TRUE.equals(v.getInativo()))
                .filter(v -> VendedorPrioritarioUtil.safe(v.getSegmentos())
                        .contains(cliente.getSegmento()))
                .filter(VendedorPrioritarioUtil::isPrioritario)
                .collect(Collectors.toList());

        if (elegiveis.isEmpty()) return Optional.empty();

        Collections.shuffle(elegiveis);

        return elegiveis.stream()
                .min(Comparator.comparingInt(VendedorPrioritarioUtil::prioridadeValor));
    }
}
