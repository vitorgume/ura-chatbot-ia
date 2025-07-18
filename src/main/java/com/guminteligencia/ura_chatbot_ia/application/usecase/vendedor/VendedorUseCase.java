package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.EscolhaNaoIdentificadoException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.VendedorNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.VendedorGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendedorUseCase {

    private final EscolhaVendedorFactory escolhaVendedorFactory;
    private final VendedorGateway gateway;
    private final Random random = new Random();
    private static String ultimoVendedor = null;


    public Vendedor escolherVendedor(Cliente cliente) {
        try {
            EscolhaVendedorType escolhaVendedorType = escolhaVendedorFactory.escolha(cliente.getSegmento(), cliente.getRegiao());
            EscolhaVendedor escolhaVendedor = escolhaVendedorType.escolher();

            if(escolhaVendedor.isRoleta()) {
                return consultarVendedor(this.roletaVendedores(escolhaVendedor.getVendedor()));
            } else {
                return consultarVendedor(escolhaVendedor.getVendedor());
            }

        } catch (EscolhaNaoIdentificadoException ex) {
            log.warn("Parâmetro de escolha de segmentos de vendedores inválida.");
            return null;
        }
    }

    public String roletaVendedores(String excecao) {
        List<Vendedor> vendedores;

        if (excecao == null) {
            vendedores = gateway.listar();
        } else {
            vendedores = gateway.listarComExcecao(excecao);
        }

        if (vendedores.size() <= 1) return vendedores.get(0).getNome();

        Vendedor vendedor;
        do {
            vendedor = vendedores.get(random.nextInt(vendedores.size()));
        } while (vendedor.getInativo() || vendedor.getNome().equals(ultimoVendedor));

        ultimoVendedor = vendedor.getNome();
        return vendedor.getNome();

    }

    public Vendedor consultarVendedor(String nome) {
        Optional<Vendedor> vendedor = gateway.consultarVendedor(nome);

        if (vendedor.isEmpty()) {
            throw new VendedorNaoEncontradoException();
        }

        return vendedor.get();
    }

    public Vendedor roletaVendedoresConversaInativa(Cliente cliente) {
        if(cliente.getSegmento() != null) {
            return escolherVendedor(cliente);
        }

        return this.consultarVendedor(this.roletaVendedores("Nilza"));
    }
}
