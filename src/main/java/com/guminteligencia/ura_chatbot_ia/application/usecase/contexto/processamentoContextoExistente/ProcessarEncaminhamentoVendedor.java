package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.application.usecase.CrmUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.VendedorUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.StatusConversa;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(3)
public class ProcessarEncaminhamentoVendedor implements ProcessamentoContextoExistenteType {

    private final MensagemUseCase mensagemUseCase;
    private final MensagemBuilder mensagemBuilder;
    private final VendedorUseCase vendedorUseCase;
    private final CrmUseCase crmUseCase;

    @Override
    public void processar(String resposta, ConversaAgente conversaAgente, Cliente cliente) {
        Vendedor vendedor = vendedorUseCase.roletaVendedores(null);
        mensagemUseCase.enviarContatoVendedor(vendedor, cliente);
        mensagemUseCase.enviarMensagem(mensagemBuilder.getMensagem(TipoMensagem.MENSAGEM_DIRECIONAMENTO_VENDEDOR, vendedor.getNome(), null), cliente.getTelefone(), false);
        mensagemUseCase.enviarContatoCliente(cliente, vendedor);

        conversaAgente.setVendedor(vendedor);
        conversaAgente.setFinalizada(true);
        conversaAgente.setStatus(StatusConversa.ATIVO);

        crmUseCase.atualizarCrm(vendedor, cliente, conversaAgente);
    }

    @Override
    public boolean deveProcessar(String resposta, ConversaAgente conversaAgente) {
        if (resposta == null) return false;

        String textoNormalizado = resposta
                .toLowerCase()
                .replaceAll("\\s+", "");

        return textoNormalizado.contains("encaminhar:true");
    }
}
