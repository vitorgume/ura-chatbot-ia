package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class OutroContatoNaoEncontradoException extends RuntimeException {

    public OutroContatoNaoEncontradoException() {
        super("Outro contato não encontrado.");
    }
}
