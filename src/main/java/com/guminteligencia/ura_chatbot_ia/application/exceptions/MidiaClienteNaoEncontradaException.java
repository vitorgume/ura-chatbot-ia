package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class MidiaClienteNaoEncontradaException extends RuntimeException {

    public MidiaClienteNaoEncontradaException() {
        super("Midia do cliente não encontrada.");
    }
}
