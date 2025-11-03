package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.VendedorUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.StatusConversa;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ConversaInativaUseCase {

    private final ConversaAgenteUseCase conversaAgenteUseCase;
    private final VendedorUseCase vendedorUseCase;
    private final CrmUseCase crmUseCase;
    private final MensagemUseCase mensagemUseCase;
    private final MensagemBuilder mensagemBuilder;

    @Value("${spring.profiles.active}")
    private final String profile;

    public ConversaInativaUseCase (
        ConversaAgenteUseCase conversaAgenteUseCase,
        VendedorUseCase vendedorUseCase,
        CrmUseCase crmUseCase,
        MensagemUseCase mensagemUseCase,
        MensagemBuilder mensagemBuilder,
        @Value("${spring.profiles.active}") String profile
    ) {
        this.conversaAgenteUseCase = conversaAgenteUseCase;
        this.vendedorUseCase = vendedorUseCase;
        this.crmUseCase = crmUseCase;
        this.mensagemUseCase = mensagemUseCase;
        this.mensagemBuilder = mensagemBuilder;
        this.profile = profile;
    }

    @Scheduled(cron = "${app.cron.conversas.inativas}")
    public void verificaAusenciaDeMensagem() {
        List<ConversaAgente> conversas = conversaAgenteUseCase.listarNaoFinalizados();
        log.info("Verificando se existe alguma mensagem inativa por mais do tempo determinado Conversas: {}", conversas);


        LocalDateTime agora = LocalDateTime.now();

        List<ConversaAgente> conversasAtrasadas = conversas.stream()
                .filter(conversa -> {
                            if(conversa.getDataUltimaMensagem() != null) {
                                if (conversa.getStatus().getCodigo().equals(3)) {
                                    return profile.equals("prod")
                                            ? conversa.getDataUltimaMensagem().plusHours(1).plusMinutes(30).isBefore(agora)
                                            : conversa.getDataUltimaMensagem().plusSeconds(10).isBefore(agora);
                                } else {
                                    return profile.equals("prod")
                                            ? conversa.getDataUltimaMensagem().plusHours(12).isBefore(agora)
                                            : conversa.getDataUltimaMensagem().plusSeconds(20).isBefore(agora);
                                }
                            }

                            return false;
                        }
                )
                .toList();


        if(!conversasAtrasadas.isEmpty()) {
            conversasAtrasadas.forEach(conversa -> {

                if(!conversa.getFinalizada() && !conversa.getStatus().getCodigo().equals(0)) {
                    conversa.setStatus(StatusConversa.INATIVO_G1);
                    mensagemUseCase.enviarMensagem(mensagemBuilder.getMensagem(TipoMensagem.RECONTATO_INATIVO_G1, null, null), conversa.getCliente().getTelefone(), false);
                    conversa.setDataUltimaMensagem(LocalDateTime.now());
                } else {
                    conversa.setStatus(StatusConversa.INATIVO_G2);
                    conversa.setFinalizada(true);
                    Vendedor vendedor = vendedorUseCase.roletaVendedoresConversaInativa(conversa.getCliente());
                    conversa.setVendedor(vendedor);
                    crmUseCase.atualizarCrm(vendedor, conversa.getCliente(), conversa);
                }

                conversaAgenteUseCase.salvar(conversa);
            });
        }

        log.info("Verificação concluida com sucesso.");
    }
}
