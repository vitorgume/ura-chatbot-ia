package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class AdministradorJaExisteException extends RuntimeException {
    public AdministradorJaExisteException() {
        super("Administrador já cadastrado com esse email.");
    }
}
