package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Order(2)
public class ProcessarContextoNaoFinalizadoNaoQualificado implements ProcessamentoContextoExistenteType {

    private final MensagemUseCase mensagemUseCase;

    @Override
    public void processar(String resposta, ConversaAgente conversaAgente, Cliente cliente) {
        mensagemUseCase.enviarMensagem(resposta, conversaAgente.getCliente().getTelefone());
    }

    @Override
    public boolean deveProcessar(String resposta, ConversaAgente conversaAgente) {
        return !conversaAgente.getFinalizada();
    }
}
