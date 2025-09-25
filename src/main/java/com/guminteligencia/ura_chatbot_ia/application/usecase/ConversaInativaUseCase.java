package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.VendedorUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("prod")
public class ConversaInativaUseCase {

    private final ConversaAgenteUseCase conversaAgenteUseCase;
    private final VendedorUseCase vendedorUseCase;
    private final MensagemUseCase mensagemUseCase;
    private final MensagemBuilder mensagemBuilder;
        private final RelatorioUseCase relatorioUseCase;

    @Scheduled(cron = "0 */20 * * * *")
    public void verificaAusenciaDeMensagem() {
        List<ConversaAgente> conversas = conversaAgenteUseCase.listarNaoFinalizados();
        log.info("Verificando se existe alguma mensagem inativa por mais de 30 minutos. Conversas: {}", conversas);


        LocalDateTime agora = LocalDateTime.now();

        List<ConversaAgente> conversasAtrasadas = conversas.stream()
                .filter(conversa -> {
                            if(conversa.getDataUltimaMensagem() != null)
                                return conversa.getDataUltimaMensagem().plusMinutes(30).isBefore(agora);

                            return false;
                        }
                )
                .toList();


        if(!conversasAtrasadas.isEmpty()) {
            conversasAtrasadas.forEach(conversa -> {
                conversa.setFinalizada(true);
                Vendedor vendedor = vendedorUseCase.roletaVendedoresConversaInativa(conversa.getCliente());
                conversa.setVendedor(vendedor);
                conversa.setInativa(true);
                mensagemUseCase
                        .enviarContatoVendedor(
                                vendedor,
                                conversa.getCliente()
                        );
                mensagemUseCase.enviarMensagem(mensagemBuilder.getMensagem(TipoMensagem.CONTATO_INATIVO, null, null), vendedor.getTelefone(), false);
                relatorioUseCase.atualizarRelatorioOnline(conversa.getCliente(), vendedor);
                conversaAgenteUseCase.salvar(conversa);
            });
        }

        log.info("Verificação concluida com sucesso.");
    }
}
