package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;

public interface MensagemGateway {
    void enviar(String resposta, String telefone);

    void enviarContato(String telefoneReceptivo, String telefoneContato, String nomeContato);
    void enviarRelatorio(String arquivo, String fileName, String telefone);
}
