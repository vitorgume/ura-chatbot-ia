package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.application.usecase.CrmUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.VendedorUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(2)
@Slf4j
public class ProcessarConversaInativa implements ProcessamentoContextoExistenteType {

    private final VendedorUseCase vendedorUseCase;
    private final CrmUseCase crmUseCase;
    private final MensagemUseCase mensagemUseCase;
    private final MensagemBuilder mensagemBuilder;

    @Override
    public void processar(String resposta, ConversaAgente conversaAgente, Cliente cliente) {
        log.info("Processando conversa inativa. Resposta: {}, Conversa: {}, Cliente: {}", resposta, conversaAgente, cliente);
        conversaAgente.setFinalizada(true);
        Vendedor vendedor = vendedorUseCase.roletaVendedoresConversaInativa(cliente);
        conversaAgente.setVendedor(vendedor);
        mensagemUseCase.enviarMensagem(mensagemBuilder.getMensagem(TipoMensagem.RECONTATO_INATIVO_G1_DIRECIONAMENTO_VENDEDOR, vendedor.getNome(), null), cliente.getTelefone(), false);
        mensagemUseCase.enviarContatoVendedor(vendedor, cliente);
        crmUseCase.atualizarCrm(vendedor, cliente, conversaAgente);
        log.info("Processamento de conversa inativa concluida com sucesso.");
    }

    @Override
    public boolean deveProcessar(String resposta, ConversaAgente conversaAgente) {
        return conversaAgente.getStatus().getCodigo().equals(0);
    }
}
