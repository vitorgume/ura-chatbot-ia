package com.guminteligencia.ura_chatbot_ia.infrastructure.repository;

import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ContextoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ContextoRepository {

    private final DynamoDbEnhancedClient enhancedClient;

    private DynamoDbTable<ContextoEntity> getTable() {
        return enhancedClient.table("contextos", TableSchema.fromBean(ContextoEntity.class));
    }

    public void deletar(UUID id) {
        ContextoEntity chave = new ContextoEntity();
        chave.setId(id);
        getTable().deleteItem(chave);
    }
}
