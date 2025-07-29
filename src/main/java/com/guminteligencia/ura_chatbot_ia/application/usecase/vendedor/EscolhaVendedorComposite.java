package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.EscolhaNaoIdentificadoException;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EscolhaVendedorComposite {

    private final List<EscolhaVendedorType> escolhas;

    public Optional<Vendedor> escolher(Cliente cliente, List<Vendedor> candidatos) {
        return escolhas.stream()
                .map(escolha -> escolha.escolher(cliente, candidatos))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

}
