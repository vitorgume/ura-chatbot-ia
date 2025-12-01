package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.MensagemContexto;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MensagemContextoListConverterTest {

    private final MensagemContextoListConverter converter = new MensagemContextoListConverter();

    @Test
    void transformFrom_deveRetornarNuloQuandoListaVaziaOuNull() {
        AttributeValue fromNull = converter.transformFrom(null);
        AttributeValue fromEmpty = converter.transformFrom(Collections.emptyList());

        assertTrue(Boolean.TRUE.equals(fromNull.nul()));
        assertTrue(Boolean.TRUE.equals(fromEmpty.nul()));
    }

    @Test
    void transformFrom_deveConverterListaParaJsonString() {
        List<MensagemContexto> lista = List.of(
                MensagemContexto.builder().mensagem("a").imagemUrl("img").audioUrl("aud").build(),
                MensagemContexto.builder().mensagem("b").build()
        );

        AttributeValue av = converter.transformFrom(lista);
        assertNull(av.nul());
        assertNotNull(av.s());

        List<MensagemContexto> back = converter.transformTo(av);
        assertEquals(lista, back);
    }

    @Test
    void transformTo_deveRetornarListaVaziaQuandoAttributeValueNulo() {
        assertTrue(converter.transformTo(null).isEmpty());

        AttributeValue avNul = AttributeValue.builder().nul(true).build();
        assertTrue(converter.transformTo(avNul).isEmpty());
    }

    @Test
    void transformTo_deveLancarRuntimeExceptionQuandoJsonInvalido() {
        AttributeValue av = AttributeValue.builder().s("invalid-json").build();
        assertThrows(RuntimeException.class, () -> converter.transformTo(av));
    }

    @Test
    void metadadosDevemSerOsEsperados() {
        assertEquals(AttributeValueType.S, converter.attributeValueType());
        assertEquals(EnhancedType.listOf(MensagemContexto.class), converter.type());
    }
}

