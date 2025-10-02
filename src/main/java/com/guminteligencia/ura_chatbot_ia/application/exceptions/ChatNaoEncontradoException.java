package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class ChatNaoEncontradoException extends RuntimeException {

    public ChatNaoEncontradoException() {
        super("Chat não encontrado.");
    }
}
