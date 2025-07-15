package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TipoMensagem {
    MENSAGEM_DIRECIONAMENTO_VENDEDOR(0, "Mensagem para direcionamento do vendedor"),
    DADOS_CONTATO_VENDEDOR(1, "Mensagem para os dados do contato enviado ao vendedor"),
    MENSAGEM_SEPARACAO(2, "Mensagem para separação entre contatos enviados ao vendedor");

    private final Integer codigo;
    private final String descricao;
}
