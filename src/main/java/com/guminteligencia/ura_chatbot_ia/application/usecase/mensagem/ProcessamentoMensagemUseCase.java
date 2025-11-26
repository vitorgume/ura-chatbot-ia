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
import java.util.Objects;
import java.util.concurrent.Semaphore;

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

    private final Semaphore processingSemaphore = new Semaphore(3);

    @Scheduled(fixedDelay = 5000)
    public void consumirFila() {

        long inicio = System.currentTimeMillis();
        log.info("[INICIO] processamentoDeMensagens");

        if (!processingSemaphore.tryAcquire()) {
            log.warn("Processamento anterior ainda em andamento, pulando esta execução");
            return;
        }

        try {
            log.info("Consumindo mensagens da fila.");

            var recebidas = mensageriaUseCase.listarAvisos();

            List<Contexto> contextosRecebidos = recebidas.stream()
                    .map(avisoContexto -> {
                        var contexto = contextoUseCase.consultarPeloId(avisoContexto.getIdContexto());
                        contextoUseCase.deletar(contexto.getId());
                        contexto.setMensagemFila(avisoContexto.getMensagemFila());
                        return contexto;
                    })
                    .filter(Objects::nonNull)
                    .toList();

            log.info("Recebidas da SQS: {}", recebidas.size());

            var processaveis = contextosRecebidos.stream()
                    .filter(contextoValidadorComposite::permitirProcessar)
                    .toList();

            var ignoradas = contextosRecebidos.stream()
                    .filter(c -> !contextoValidadorComposite.permitirProcessar(c))
                    .toList();

            log.info("Processáveis: {}, Ignoradas: {}", processaveis.size(), ignoradas.size());

            processaveis.forEach(contexto -> {
                try {
                    log.info("Processando: id={}, tel={}", contexto.getId(), contexto.getTelefone());
                    processarMensagem(contexto);
                    mensageriaUseCase.deletarMensagem(contexto.getMensagemFila());

                } catch (Exception e) {
                    log.error("Falha ao processar id={}, tel={}.",
                            contexto.getId(), contexto.getTelefone(), e);
                    mensageriaUseCase.deletarMensagem(contexto.getMensagemFila());
                }
            });

            ignoradas.forEach(contexto -> {
                log.info("Ignorando: {}", contexto);
                mensageriaUseCase.deletarMensagem(contexto.getMensagemFila());
            });

        } finally {
            long total = System.currentTimeMillis() - inicio;
            log.info("[FIM] processamentoDeMensagens - tempoTotal={}ms", total);
            processingSemaphore.release();
            log.info("Consumo concluído.");
        }


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
