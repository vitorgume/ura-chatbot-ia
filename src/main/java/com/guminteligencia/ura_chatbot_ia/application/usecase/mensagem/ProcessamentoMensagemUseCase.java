package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem;

import com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.ContextoUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente.ProcessamentoContextoExistente;
import com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.ProcessamentoContextoNovoUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.*;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.validador.ContextoValidadorComposite;
import com.guminteligencia.ura_chatbot_ia.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessamentoMensagemUseCase {

    private final MensageriaUseCase mensageriaUseCase;
    private final ContextoUseCase contextoUseCase;
    private final ClienteUseCase clienteUseCase;
    private final ContextoValidadorComposite contextoValidadorComposite;
    private final ProcessamentoContextoExistente processamentoContextoExistente;
    private final ProcessamentoContextoNovoUseCase processamentoContextoNovoUseCase;

    @Scheduled(fixedDelay = 5000)
    public void consumirFila() {
        log.info("Consumindo mensagens da fila.");
        List<Contexto> contextos = mensageriaUseCase.listarContextos().stream().filter(contextoValidadorComposite::deveIgnorar).toList();;

        contextos.forEach(contexto -> {
            this.processarMensagem(contexto);

            mensageriaUseCase.deletarMensagem(contexto.getMensagemFila());

            contextoUseCase.deletar(contexto.getId());
        });
        log.info("Mensagens consumidas com sucesso. Contextos: {}", contextos);
    }

    private void processarMensagem(Contexto contexto) {
        log.info("Processando nova mensagem. Contexto: {}", contexto);

        clienteUseCase.consultarPorTelefone(contexto.getTelefone())
                .ifPresentOrElse(
                        cl -> processamentoContextoExistente.processarContextoExistente(cl, contexto),
                        () -> processamentoContextoNovoUseCase.processarContextoNovo(contexto)
                );

        log.info("Mensagem nova processada com sucesso.");
    }
}
