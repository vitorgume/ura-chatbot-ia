package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.mapper.EnumMapper;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.VendedorUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessamentoMensagemUseCase {

    private final MensageriaUseCase mensageriaUseCase;
    private final ContextoUseCase contextoUseCase;
    private final ClienteUseCase clienteUseCase;
    private final AgenteUseCase agenteUseCase;
    private final MensagemUseCase mensagemUseCase;
    private final ConversaAgenteUseCase conversaAgenteUseCase;
    private final VendedorUseCase vendedorUseCase;
    private final MensagemBuilder mensagemBuilder;

    @Scheduled(fixedDelay = 5000)
    public void consumirFila() {
        log.info("Consumindo mensagens da fila.");
        List<Contexto> contextos = this.filtraContextosPeloStatus(mensageriaUseCase.listarContextos());

        contextos.forEach(contexto -> {
            this.processarMensagem(contexto);

            mensageriaUseCase.deletarMensagem(contexto.getMensagemFila());

            contextoUseCase.deletar(contexto.getId());
        });
        log.info("Mensagens consumidas com sucesso. Contextos: {}", contextos);
    }

    private void processarMensagem(Contexto contexto) {
        log.info("Processando nova mensagem. Contexto: {}", contexto);

        Optional<Cliente> cliente = clienteUseCase.consultarPorTelefone(contexto.getTelefone());

        RespostaAgente resposta;

        if (cliente.isEmpty()) {
            Cliente clienteSalvo = clienteUseCase.cadastrar(contexto.getTelefone());
            ConversaAgente novaConversa = conversaAgenteUseCase.criar(clienteSalvo);
            resposta = agenteUseCase.enviarMensagem(clienteSalvo, novaConversa, contexto.getMensagens());
            mensagemUseCase.enviarMensagem(resposta.getResposta(), clienteSalvo.getTelefone());
        } else {
            ConversaAgente conversaAgente = conversaAgenteUseCase.consultarPorCliente(cliente.get().getId());
            resposta = agenteUseCase.enviarMensagem(cliente.get(), conversaAgente, contexto.getMensagens());

            if (resposta.getQualificacao().getQualificado()) {
                Cliente clienteQualificado = Cliente.builder()
                        .nome(resposta.getQualificacao().getNome())
                        .regiao(EnumMapper.regiaoMapper(resposta.getQualificacao().getRegiao()))
                        .segmento(EnumMapper.segmentoMapper(resposta.getQualificacao().getSegmento()))
                        .build();

                Cliente clienteSalvo = clienteUseCase.alterar(clienteQualificado, conversaAgente.getCliente().getId());
                Vendedor vendedor = vendedorUseCase.escolherVendedor(clienteSalvo);
                mensagemUseCase.enviarMensagem(mensagemBuilder.getMensagem(TipoMensagem.MENSAGEM_DIRECIONAMENTO_VENDEDOR, vendedor.getNome(), null), clienteSalvo.getTelefone());
                mensagemUseCase.enviarContatoVendedor(vendedor, clienteSalvo);
                conversaAgente.setVendedor(vendedor);
                conversaAgente.setFinalizada(true);
                conversaAgenteUseCase.salvar(conversaAgente);
            }

            mensagemUseCase.enviarMensagem(resposta.getResposta(), conversaAgente.getCliente().getTelefone());
        }

        log.info("Mensagem nova processada com sucesso.");
    }


    private List<Contexto> filtraContextosPeloStatus(List<Contexto> contextos) {
        return contextos.stream().filter(contexto -> {
                    Contexto contextoSalvo = contextoUseCase.consultarPeloId(contexto.getId());

                    return contextoSalvo.getStatus().getCodigo() == 1;
                })
                .toList();
    }
}
