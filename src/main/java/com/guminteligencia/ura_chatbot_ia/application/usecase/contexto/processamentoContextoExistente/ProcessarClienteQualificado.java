package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.application.mapper.EnumMapper;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ClienteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ConversaAgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.VendedorUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.RespostaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(1)
@RequiredArgsConstructor
public class ProcessarClienteQualificado implements ProcessamentoContextoExistenteType {

    private final VendedorUseCase vendedorUseCase;
    private final ClienteUseCase clienteUseCase;
    private final MensagemUseCase mensagemUseCase;
    private final MensagemBuilder mensagemBuilder;

    @Override
    public void processar(RespostaAgente resposta, ConversaAgente conversaAgente, Cliente cliente) {
        Cliente clienteQualificado = Cliente.builder()
                .nome(resposta.getQualificacao().getNome())
                .regiao(EnumMapper.regiaoMapper(resposta.getQualificacao().getRegiao()))
                .segmento(EnumMapper.segmentoMapper(resposta.getQualificacao().getSegmento()))
                .descricaoMaterial(resposta.getQualificacao().getDescricaoMaterial())
                .build();

        Cliente clienteSalvo = clienteUseCase.alterar(clienteQualificado, conversaAgente.getCliente().getId());
        Vendedor vendedor = vendedorUseCase.escolherVendedor(clienteSalvo);
        mensagemUseCase.enviarMensagem(mensagemBuilder.getMensagem(TipoMensagem.MENSAGEM_DIRECIONAMENTO_VENDEDOR, vendedor.getNome(), null), clienteSalvo.getTelefone());
        mensagemUseCase.enviarContatoVendedor(vendedor, clienteSalvo);
        conversaAgente.setVendedor(vendedor);
        conversaAgente.setFinalizada(true);
    }

    @Override
    public boolean deveProcessar(RespostaAgente resposta, ConversaAgente conversaAgente) {
        return resposta.getQualificacao().getQualificado() && !conversaAgente.getFinalizada();
    }
}
