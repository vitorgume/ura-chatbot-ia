package com.guminteligencia.ura_chatbot_ia.infrastructure.repository;

import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ContextoEntity;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ContextoRepository {

    private final DynamoDbTemplate dynamoDbTemplate;

    public void deletar(ContextoEntity contextoEntity) {
        dynamoDbTemplate.delete(contextoEntity);
    }

    public Optional<ContextoEntity> consultarPorId(UUID id) {
        ContextoEntity contexto = dynamoDbTemplate.load(Key.builder()
                        .partitionValue(id.toString())
                        .build()
                ,ContextoEntity.class
        );

        return contexto == null ? Optional.empty() : Optional.of(contexto);
    }
}
