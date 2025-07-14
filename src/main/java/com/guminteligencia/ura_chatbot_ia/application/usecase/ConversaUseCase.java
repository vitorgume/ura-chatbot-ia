package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.ConversaAgenteNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.ConversaAgenteGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.RespostaAgente;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversaUseCase {

    private final ConversaAgenteGateway gateway;
    private final MensageriaUseCase mensageriaUseCase;
    private final ContextoUseCase contextoUseCase;
    private final ClienteUseCase clienteUseCase;
    private final AgenteUseCase agenteUseCase;
    private final MensagemUseCase mensagemUseCase;

    @Scheduled(fixedDelay = 5000)
    public void consumirFila() {
        List<Contexto> contextos = mensageriaUseCase.listarContextos();

        contextos.forEach(contexto -> {
            this.processarMensagem(contexto);

            mensageriaUseCase.deletarMensagem(contexto.getMensagemFila());

            contextoUseCase.deletar(contexto.getId());
        });
    }

    private void processarMensagem(Contexto contexto) {
        Optional<Cliente> cliente = clienteUseCase.consultarPorTelefone(contexto.getTelefone());

        RespostaAgente resposta;

        if(cliente.isEmpty()) {
            Cliente clienteSalvo = clienteUseCase.cadastrar(contexto.getTelefone());
            ConversaAgente novaConversa = this.criar(clienteSalvo);
            resposta = agenteUseCase.enviarMensagem(clienteSalvo, novaConversa, contexto.getMensagens());
            mensagemUseCase.enviarMensagem(resposta.getResposta(), clienteSalvo.getTelefone());
        } else {
            ConversaAgente conversaAgente = this.consultarPorCliente(cliente.get().getId());
            resposta = agenteUseCase.enviarMensagem(cliente.get(), conversaAgente, contexto.getMensagens());
        }
    }

    public ConversaAgente criar(Cliente cliente) {
        ConversaAgente conversaAgente = ConversaAgente.builder()
                .cliente(cliente)
                .dataCriacao(LocalDateTime.now())
                .build();

        return gateway.salvar(conversaAgente);
    }

    public ConversaAgente consultarPorCliente(UUID id) {
        Optional<ConversaAgente> conversaAgente = gateway.consultarPorId(id);

        if(conversaAgente.isEmpty()) throw new ConversaAgenteNaoEncontradoException();

        return conversaAgente.get();
    }
}
