package br.com.fiap.esgresiduos.dto;

import br.com.fiap.esgresiduos.entity.Coleta;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de saída com os dados de uma Coleta registrada.
 */
public record ColetaResponse(

    Long id,
    Long idPontoColeta,
    String nomePontoColeta,
    Long idUsuario,
    String nomeUsuario,
    Long idResiduo,
    String nomeResiduo,
    BigDecimal quantidadeKg,
    String observacao,
    Coleta.TipoOperacao tipoOperacao,
    LocalDateTime dataColeta

) {
    /**
     * Método fábrica estático que converte uma entidade {@link Coleta}
     * em um {@link ColetaResponse}.
     */
    public static ColetaResponse fromEntity(Coleta coleta) {
        return new ColetaResponse(
            coleta.getId(),
            coleta.getPontoColeta().getId(),
            coleta.getPontoColeta().getNome(),
            coleta.getUsuario().getId(),
            coleta.getUsuario().getNome(),
            coleta.getResiduo() != null ? coleta.getResiduo().getId()   : null,
            coleta.getResiduo() != null ? coleta.getResiduo().getNome() : null,
            coleta.getQuantidadeKg(),
            coleta.getObservacao(),
            coleta.getTipoOperacao(),
            coleta.getDataColeta()
        );
    }
}
