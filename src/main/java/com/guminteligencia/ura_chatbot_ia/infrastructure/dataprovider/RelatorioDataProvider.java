package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.RelatorioGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.RelatorioOnlineDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.mensagem.WebClientExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RelatorioDataProvider implements RelatorioGateway {

    private final WebClientExecutor executor;

    @Value("${gume.uri.relatorio.online}")
    private final String uri;

    @Value("${spring.profiles.active}")
    private final String profile;

    public RelatorioDataProvider(
            WebClientExecutor executor,
            @Value("${gume.uri.relatorio.online}") String uri,
            @Value("${spring.profiles.active}") String profile
    ) {
        this.executor = executor;
        this.uri = uri;
        this.profile = profile;
    }

    @Override
    public void atualizarRelatorioOnline(RelatorioOnlineDto novaLinha) {
        if(profile.equals("prod")) {
            executor.post(uri, novaLinha, Map.of(), "Erro ao atualizar relatório online.");
        } else {
            System.out.println("Relatório online atualizado. Nova linha: " + novaLinha);
        }
    }
}
