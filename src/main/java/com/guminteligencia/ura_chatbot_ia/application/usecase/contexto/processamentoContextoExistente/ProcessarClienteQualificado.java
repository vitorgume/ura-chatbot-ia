package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.application.mapper.EnumMapper;
import com.guminteligencia.ura_chatbot_ia.application.usecase.AgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ClienteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.CrmUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.VendedorUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.Qualificacao;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class ProcessarClienteQualificado implements ProcessamentoContextoExistenteType {

    private final VendedorUseCase vendedorUseCase;
    private final ClienteUseCase clienteUseCase;
    private final MensagemUseCase mensagemUseCase;
    private final MensagemBuilder mensagemBuilder;
    private final AgenteUseCase agenteUseCase;
    private final CrmUseCase crmUseCase;

    @Override
    public void processar(String resposta, ConversaAgente conversaAgente, Cliente cliente) {
        log.info("Processando cliente qualificado. Resposta: {}, ConversaAgente: {}, Cliente: {}", resposta, conversaAgente, cliente);
        Qualificacao qualificacao = agenteUseCase.enviarJsonTrasformacao(resposta);

        Cliente clienteQualificado = Cliente.builder()
                .nome(qualificacao.getNome())
                .regiao(EnumMapper.regiaoMapper(qualificacao.getRegiao()))
                .segmento(EnumMapper.segmentoMapper(qualificacao.getSegmento()))
                .descricaoMaterial(qualificacao.getDescricaoMaterial())
                .enderecoReal(qualificacao.getEnderecoReal())
                .build();

        Cliente clienteSalvo = clienteUseCase.alterar(clienteQualificado, conversaAgente.getCliente().getId());
        Vendedor vendedor = vendedorUseCase.escolherVendedor(clienteSalvo);
        mensagemUseCase.enviarMensagem(mensagemBuilder.getMensagem(TipoMensagem.MENSAGEM_DIRECIONAMENTO_VENDEDOR, vendedor.getNome(), null), clienteSalvo.getTelefone(), false);
        crmUseCase.atualizarCrm(vendedor, clienteSalvo, conversaAgente);

        if(vendedor.getNome().equals("Nilza")) {
            mensagemUseCase.enviarContatoVendedor(vendedor, cliente);
        }

        conversaAgente.setVendedor(vendedor);
        conversaAgente.setFinalizada(true);
        log.info("Processamento de cliente qualificado concluido com sucesso.");
    }

    @Override
    public boolean deveProcessar(String resposta, ConversaAgente conversaAgente) {
        return isQualificado(resposta);
    }

    private boolean isQualificado(String resposta) {
        if (resposta == null) return false;

        String textoNormalizado = resposta
                .toLowerCase()
                .replaceAll("\\s+", "");

        return textoNormalizado.contains("qualificado:true");
    }
}
