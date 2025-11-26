package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MapperConstructorsTest {

    @Test
    void deveInstanciarMappersParaCobrirConstrutoresPadrao() {
        assertNotNull(new AdministradorMapper());
        assertNotNull(new ChatMapper());
        assertNotNull(new ClienteMapper());
        assertNotNull(new LoginMapper());
        assertNotNull(new MensagemConversaMapper());
        assertNotNull(new VendedorMapper());
    }
}
