package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.application.usecase.AgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ConversaAgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.RespostaAgente;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessamentoContextoExistente {

    private final AgenteUseCase agenteUseCase;
    private final ConversaAgenteUseCase conversaAgenteUseCase;
    private final ProcessarContextoExistenteFactory processarContextoExistenteFactory;

    public void processarContextoExistente(Cliente cliente, Contexto contexto) {
        log.info("Processando contexto existente. Cliente: {}, Contexto: {}", cliente, contexto);

        ConversaAgente conversaAgente = conversaAgenteUseCase.consultarPorCliente(cliente.getId());
        RespostaAgente resposta = agenteUseCase.enviarMensagem(cliente, conversaAgente, contexto.getMensagens());

        ProcessamentoContextoExistenteType processo = processarContextoExistenteFactory.create(resposta, conversaAgente);
        processo.processar(resposta, conversaAgente, cliente);

        conversaAgente.setDataUltimaMensagem(LocalDateTime.now());
        conversaAgenteUseCase.salvar(conversaAgente);

        log.info("Processamento de contexto existente concluido com sucesso.");
    }
}
