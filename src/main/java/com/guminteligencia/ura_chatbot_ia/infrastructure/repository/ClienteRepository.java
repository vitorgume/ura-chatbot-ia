package com.guminteligencia.ura_chatbot_ia.infrastructure.repository;

import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity, UUID> {
    Optional<ClienteEntity> findByTelefone(String telefone);

    @Query(value = """
                  (
                    SELECT cl.nome, cl.telefone, cl.segmento, cl.regiao, co.data_criacao, v.nome AS nome_vendedor
                    FROM clientes cl
                    INNER JOIN conversas_agente co ON co.cliente_id_cliente = cl.id_cliente
                    INNER JOIN vendedores v ON v.id_vendedor = co.vendedor_id_vendedor
                    WHERE co.data_criacao >= DATE_FORMAT(DATE_SUB(DATE_ADD(NOW(), INTERVAL 3 HOUR), INTERVAL 1 DAY), '%Y-%m-%d 16:00:00')
                      AND co.data_criacao <= DATE_FORMAT(DATE_SUB(DATE_ADD(NOW(), INTERVAL 3 HOUR), INTERVAL 1 DAY), '%Y-%m-%d 23:59:59')
                )
                UNION ALL
                (
                    SELECT cl.nome, cl.telefone, cl.segmento, cl.regiao, co.data_criacao, v.nome AS nome_vendedor
                    FROM clientes cl
                    INNER JOIN conversas_agente co ON co.cliente_id_cliente = cl.id_cliente
                    INNER JOIN vendedores v ON v.id_vendedor = co.vendedor_id_vendedor
                    WHERE co.data_criacao >= DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 3 HOUR), '%Y-%m-%d 00:00:00')
                      AND co.data_criacao <= DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 3 HOUR), '%Y-%m-%d 16:00:00')
                );
            """, nativeQuery = true)
    List<Object[]> gerarRelatorio();

    @Query(value = """
                SELECT cl.nome, cl.telefone, cl.segmento, cl.regiao, co.data_criacao, v.nome as nome_vendedor
                                FROM clientes cl
                                INNER JOIN conversas_agente co ON co.cliente_id_cliente = cl.id_cliente
                                INNER JOIN vendedores v ON v.id_vendedor = co.vendedor_id_vendedor
                                WHERE co.data_criacao >= DATE_FORMAT(DATE_SUB(DATE_ADD(NOW(), INTERVAL 3 HOUR), INTERVAL 3 DAY), '%Y-%m-%d 16:00:00')
                                  AND co.data_criacao <= DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 3 HOUR), '%Y-%m-%d 16:00:00')
            """, nativeQuery = true)
    List<Object[]> gerarRelatorioSegundaFeira();
}
