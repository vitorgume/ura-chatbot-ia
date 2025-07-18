package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class ProcessoContextoExistenteNaoIdentificadoException extends RuntimeException {

    public ProcessoContextoExistenteNaoIdentificadoException() {
        super("Processo de contexto existente não foi identificado.");
    }
}
