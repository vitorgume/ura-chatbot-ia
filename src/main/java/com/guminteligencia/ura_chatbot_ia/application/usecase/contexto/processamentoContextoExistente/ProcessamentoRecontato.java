package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.application.usecase.ConversaAgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.OutroContatoUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Order(3)
public class ProcessamentoRecontato implements ProcessamentoContextoExistenteType {

    private final MensagemUseCase mensagemUseCase;
    private final MensagemBuilder mensagemBuilder;
    private final OutroContatoUseCase outroContatoUseCase;
    private final ConversaAgenteUseCase conversaAgenteUseCase;

    @Override
    public void processar(RespostaAgente resposta, ConversaAgente conversaAgente, Cliente cliente) {
        if(!conversaAgente.getRecontato()) {
            Vendedor vendedor = conversaAgente.getVendedor();
            mensagemUseCase.enviarMensagem(mensagemBuilder.getMensagem(TipoMensagem.MENSAGEM_RECONTATO_VENDEDOR, vendedor.getNome(), null), cliente.getTelefone());
            mensagemUseCase.enviarContatoVendedor(vendedor, cliente);

            OutroContato outroContato = outroContatoUseCase.consultarPorNome("Ney");
            mensagemUseCase.enviarMensagem(mensagemBuilder.getMensagem(TipoMensagem.MENSAGEM_ALERTA_RECONTATO, vendedor.getNome(), cliente), outroContato.getTelefone());
            conversaAgente.setRecontato(true);
            conversaAgenteUseCase.salvar(conversaAgente);
        } else {
            mensagemUseCase.enviarMensagem(resposta.getResposta(), conversaAgente.getCliente().getTelefone());
        }
    }

    @Override
    public boolean deveProcessar(RespostaAgente resposta, ConversaAgente conversaAgente) {
        return true;
    }
}
