package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MapperConstructorsTest {

    @Test
    void deveInstanciarTodosMappersParaCobrirConstrutoresPadrao() {
        assertNotNull(new AdministradorMappper());
        assertNotNull(new AvisoContextoMapper());
        assertNotNull(new ChatMapper());
        assertNotNull(new ClienteMapper());
        assertNotNull(new ContextoMapper());
        assertNotNull(new ConversaAgenteMapper());
        assertNotNull(new MensagemConversaMapper());
        assertNotNull(new OutroContatoMapper());
        assertNotNull(new RelatorioMapper());
        assertNotNull(new VendedorMapper());
    }
}
