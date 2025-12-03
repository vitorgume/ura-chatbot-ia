package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity;

import com.guminteligencia.ura_chatbot_ia.domain.MensagemContexto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.MensagemContextoListConverter;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.List;
import java.util.UUID;

@DynamoDbBean
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class ContextoEntity {
    private UUID id;
    private String telefone;
    private List<MensagemContexto> mensagens;

    @DynamoDbPartitionKey
    public UUID getId() {
        return id;
    }

    public String getTelefone() {
        return telefone;
    }

    @DynamoDbConvertedBy(MensagemContextoListConverter.class)
    public List<MensagemContexto> getMensagens() {
        return mensagens;
    }

}
