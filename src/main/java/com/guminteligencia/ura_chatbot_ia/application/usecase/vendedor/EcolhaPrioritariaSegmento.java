package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import ch.qos.logback.core.net.server.Client;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Order(1)
public class EcolhaPrioritariaSegmento implements EscolhaVendedorType {

    @Override
    public Optional<Vendedor> escolher(Cliente cliente, List<Vendedor> candidatos) {
        if (cliente == null) return Optional.empty();

        return candidatos.stream()
                .filter(Objects::nonNull)
                .filter(v -> VendedorPrioritarioUtil.safe(v.getSegmentos())
                        .contains(cliente.getSegmento()))
                .filter(VendedorPrioritarioUtil::isPrioritario)
                .min(Comparator.comparingInt(VendedorPrioritarioUtil::prioridadeValor));
    }
}
