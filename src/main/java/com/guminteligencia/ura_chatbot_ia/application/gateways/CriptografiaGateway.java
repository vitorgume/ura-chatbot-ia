package com.guminteligencia.ura_chatbot_ia.application.gateways;

public interface CriptografiaGateway {
    String criptografar(String senha);

    boolean validarSenha(String senha, String senhaRepresentante);
}
