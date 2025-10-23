package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.application.usecase.AgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ConversaAgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
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

        String resposta = conversaAgente.getFinalizada()
                ? ""
                : agenteUseCase.enviarMensagem(cliente, conversaAgente, contexto.getMensagens());

        // Normaliza e evita envio de mensagens vazias/whitespace
        String respostaSan = resposta == null ? "" : resposta.trim();

        if (respostaSan.isEmpty()) {
            log.warn("Sem mensagem para enviar (conversa finalizada ou resposta vazia). Pular envio.");
            // se fizer sentido, marque como finalizado/idle aqui
            conversaAgente.setDataUltimaMensagem(LocalDateTime.now());
            conversaAgenteUseCase.salvar(conversaAgente);
            log.info("Processamento concluído sem envio.");
            return;
        }

        ProcessamentoContextoExistenteType processo =
                processarContextoExistenteFactory.create(respostaSan, conversaAgente);

        processo.processar(respostaSan, conversaAgente, cliente);

        conversaAgente.setDataUltimaMensagem(LocalDateTime.now());
        conversaAgenteUseCase.salvar(conversaAgente);

        log.info("Processamento de contexto existente concluído com sucesso.");
    }
}
