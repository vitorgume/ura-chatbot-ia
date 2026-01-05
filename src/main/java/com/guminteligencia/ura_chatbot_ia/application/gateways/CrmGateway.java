package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.CardDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.ContatoBodyDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.SessaoArquivoDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactDto;

import java.util.Optional;

public interface CrmGateway {
    Optional<ContactDto> consultaLeadPeloTelefone(String telefone);

    void atualizarContato(Integer idContato, ContatoBodyDto body);

    void atualizarCard(CardDto cardDto, Integer idLead);
}
