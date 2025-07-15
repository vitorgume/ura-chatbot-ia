package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class VendedorNaoEncontradoException extends RuntimeException {

    public VendedorNaoEncontradoException() {
        super("Vendedor não encontrado.");
    }
}
