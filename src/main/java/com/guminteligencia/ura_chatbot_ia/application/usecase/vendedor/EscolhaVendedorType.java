package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;

import java.util.List;
import java.util.Optional;

public interface EscolhaVendedorType {
    Optional<Vendedor> escolher(Cliente cliente, List<Vendedor> vendedores);
}
