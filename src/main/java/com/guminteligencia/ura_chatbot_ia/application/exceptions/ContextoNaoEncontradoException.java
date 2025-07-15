package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class ContextoNaoEncontradoException extends RuntimeException {

    public ContextoNaoEncontradoException() {
        super("Contexto não encontrado.");
    }
}
