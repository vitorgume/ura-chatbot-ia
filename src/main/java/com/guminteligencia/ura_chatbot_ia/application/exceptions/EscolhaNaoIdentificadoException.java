package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class EscolhaNaoIdentificadoException extends RuntimeException {

    public EscolhaNaoIdentificadoException() {
        super("Escolha de vendedor não identificada");
    }
}
