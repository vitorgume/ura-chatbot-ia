package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.domain.Prioridade;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class VendedorPrioritarioUtil {
    static boolean isPrioritario(Vendedor v) {
        return Optional.ofNullable(v.getPrioridade())
                .map(Prioridade::getPrioritario)
                .orElse(Boolean.FALSE);
    }

    static int prioridadeValor(Vendedor v) {
        return Optional.ofNullable(v.getPrioridade())
                .map(Prioridade::getValor)
                .orElse(Integer.MAX_VALUE);
    }

    static <T> Collection<T> safe(Collection<T> c) {
        return (c == null) ? Collections.emptyList() : c;
    }
}
