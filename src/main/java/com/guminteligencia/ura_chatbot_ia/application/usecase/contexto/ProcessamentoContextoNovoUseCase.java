package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto;

import com.guminteligencia.ura_chatbot_ia.application.usecase.AgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ClienteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ConversaAgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente.ProcessamentoContextoExistente;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
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
public class ProcessamentoContextoNovoUseCase {

    private final ClienteUseCase clienteUseCase;
    private final ConversaAgenteUseCase conversaAgenteUseCase;
    private final ProcessamentoContextoExistente processamentoContextoExistente;

    public void processarContextoNovo(Contexto contexto) {
        log.info("Processando novo contexto. Contexto: {}", contexto);

        Cliente clienteSalvo;

        clienteSalvo = clienteUseCase.consultarPorTelefone(contexto.getTelefone()).orElseGet(() -> clienteUseCase.cadastrar(contexto.getTelefone()));
        ConversaAgente novaConversa = conversaAgenteUseCase.criar(clienteSalvo);
        conversaAgenteUseCase.salvar(novaConversa);
        processamentoContextoExistente.processarContextoExistente(clienteSalvo, contexto);

        log.info("Novo contexto processado com sucesso.");
    }

}
