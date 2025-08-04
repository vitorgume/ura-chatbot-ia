package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.VendedorComMesmoTelefoneException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.VendedorNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.VendedorNaoEscolhidoException;
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

    private final EscolhaVendedorComposite escolhaVendedorComposite;
    private final VendedorGateway gateway;
    private final Random random = new Random();
    private static String ultimoVendedor = null;

    public Vendedor cadastrar(Vendedor novoVendedor) {
        log.info("Cadastrando novo vendedor. Novo vendedor: {}", novoVendedor);

        Vendedor vendedor = this.consultarPorTelefone(novoVendedor.getTelefone());

        if (vendedor.getTelefone().equals(novoVendedor.getTelefone())) {
            throw new VendedorComMesmoTelefoneException();
        }

        novoVendedor = gateway.salvar(novoVendedor);

        log.info("Novo vendedor cadastrado com sucesso. Vendedor: {}", novoVendedor);

        return novoVendedor;
    }


    public Vendedor escolherVendedor(Cliente cliente) {
        List<Vendedor> candidatos = gateway.listarAtivos();
        return escolhaVendedorComposite.escolher(cliente, candidatos).orElseThrow(VendedorNaoEscolhidoException::new);
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
        if (cliente.getSegmento() != null) {
            return escolherVendedor(cliente);
        }

        return this.consultarVendedor(this.roletaVendedores("Nilza"));
    }

    public Vendedor alterar(Vendedor novosDados, Long idVendedor) {
        log.info("Alterando dados do vendedor. Novos dados: {}, Id vendedor: {}", novosDados, idVendedor);

        Vendedor vendedor = this.consultarPorId(idVendedor);

        vendedor.setDados(novosDados);

        vendedor = gateway.salvar(vendedor);

        log.info("Alteração de novos dados concluida com sucesso. Novos dados: {}", vendedor);

        return vendedor;
    }

    public List<Vendedor> listar() {
        log.info("Listando vendedores.");

        List<Vendedor> vendedores = gateway.listar();

        log.info("Vendedores listados com sucesso. Vendedores: {}", vendedores);

        return vendedores;
    }

    public void deletar(Long idVendedor) {
        log.info("Deletando vendedor. Id vendedor: {}", idVendedor);

        this.consultarPorId(idVendedor);
        gateway.deletar(idVendedor);

        log.info("Vendedor deletado com sucesso.");
    }

    private Vendedor consultarPorId(Long idVendedor) {
        log.info("Consultando vendedor pelo id. Id vendedor: {}", idVendedor);
        Optional<Vendedor> vendedor = gateway.consultarPorId(idVendedor);

        if (vendedor.isEmpty()) {
            throw new VendedorNaoEncontradoException();
        }

        return vendedor.get();
    }

    private Vendedor consultarPorTelefone(String telefone) {
        log.info("Consultando vendedor pelo seu telefone. Telefone: {}", telefone);

        Optional<Vendedor> vendedor = gateway.consultarPorTelefone(telefone);

        if (vendedor.isEmpty()) {
            throw new VendedorNaoEncontradoException();
        }

        log.info("Vendedor consultado pelo seu telefone com sucesso. Vendedor: {}", vendedor);

        return vendedor.get();
    }
}
