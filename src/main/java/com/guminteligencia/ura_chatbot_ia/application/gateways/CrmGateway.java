package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.CardDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.SessaoArquivoDto;

import java.util.Optional;

public interface CrmGateway {
    Optional<Integer> consultaLeadPeloTelefone(String telefone);

    void atualizarCard(CardDto cardDto, Integer idLead);

    SessaoArquivoDto criarSessaoArquivo(String urlArquivo);

    String enviarArquivoParaUpload(SessaoArquivoDto sessaoArquivo, String urlArquivo);

    void anexarArquivoLead(String idArquivo, Integer idLead);
}
