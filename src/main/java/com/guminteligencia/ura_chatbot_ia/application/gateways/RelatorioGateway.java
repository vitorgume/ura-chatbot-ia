package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.RelatorioOnlineDto;

public interface RelatorioGateway {
    void atualizarRelatorioOnline(RelatorioOnlineDto novaLinha);
}
