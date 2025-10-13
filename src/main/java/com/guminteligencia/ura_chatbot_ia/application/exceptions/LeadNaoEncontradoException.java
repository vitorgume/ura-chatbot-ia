package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class LeadNaoEncontradoException extends RuntimeException {

    public LeadNaoEncontradoException() {
        super("Lead não encontrado.");
    }
}
