package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.application.usecase.ConversaAgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.CrmUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.OutroContatoUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Order(4)
@Slf4j
public class ProcessamentoRecontato implements ProcessamentoContextoExistenteType {

    private final MensagemUseCase mensagemUseCase;
    private final MensagemBuilder mensagemBuilder;
    private final OutroContatoUseCase outroContatoUseCase;
    private final ConversaAgenteUseCase conversaAgenteUseCase;

    @Override
    public void processar(String resposta, ConversaAgente conversaAgente, Cliente cliente) {
        log.info("Processando recontato. Resposta: {}, ConversaAgente: {}, Cliente: {}", resposta, conversaAgente, cliente);
        if(!conversaAgente.getRecontato()) {
            Vendedor vendedor = conversaAgente.getVendedor();
            mensagemUseCase.enviarMensagem(mensagemBuilder.getMensagem(TipoMensagem.MENSAGEM_RECONTATO_VENDEDOR, vendedor.getNome(), null), cliente.getTelefone(), false);

            OutroContato outroContato = outroContatoUseCase.consultarPorNome("Ana");
            mensagemUseCase.enviarMensagem(mensagemBuilder.getMensagem(TipoMensagem.MENSAGEM_ALERTA_RECONTATO, vendedor.getNome(), cliente), outroContato.getTelefone(), false);
            mensagemUseCase.enviarContatoVendedor(vendedor, cliente);

            conversaAgente.setRecontato(true);
            conversaAgenteUseCase.salvar(conversaAgente);
        }

        log.info("Processamente de recontato concluido com sucesso.");
    }

    @Override
    public boolean deveProcessar(String resposta, ConversaAgente conversaAgente) {
        return conversaAgente.getFinalizada();
    }
}
