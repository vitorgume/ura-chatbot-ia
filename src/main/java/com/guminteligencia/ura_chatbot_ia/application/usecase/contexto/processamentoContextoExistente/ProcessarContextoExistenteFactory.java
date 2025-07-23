package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.ProcessoContextoExistenteNaoIdentificadoException;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.RespostaAgente;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessarContextoExistenteFactory {

    private final List<ProcessamentoContextoExistenteType> processos;

    public ProcessamentoContextoExistenteType create(String resposta, ConversaAgente conversaAgente) {
        return processos.stream()
                .filter(processo -> processo.deveProcessar(resposta, conversaAgente))
                .findFirst()
                .orElseThrow(ProcessoContextoExistenteNaoIdentificadoException::new);
    }
}
