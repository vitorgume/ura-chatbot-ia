package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class VendedorNaoEscolhidoException extends RuntimeException {
    public VendedorNaoEscolhidoException() {
        super("Vendedor não escolhido de acordo com segmentação.");
    }
}
