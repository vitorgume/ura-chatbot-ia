package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class AdministradorNaoEncontradoException extends RuntimeException {

    public AdministradorNaoEncontradoException() {
        super("Administrador não encontrado.");
    }
}
