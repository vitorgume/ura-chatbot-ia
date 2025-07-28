package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;

import java.util.List;
import java.util.Optional;

public interface VendedorGateway {
    Optional<Vendedor> consultarVendedor(String nome);

    List<Vendedor> listar();

    List<Vendedor> listarComExcecao(String excecao);

    Vendedor salvar(Vendedor novoVendedor);

    Optional<Vendedor> consultarPorTelefone(String telefone);

    void deletar(Long idVendedor);

    Optional<Vendedor> consultarPorId(Long idVendedor);

    List<Vendedor> listarPorSegmento(Segmento segmento);
}
