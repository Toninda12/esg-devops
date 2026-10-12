package br.com.fiap.esgresiduos.dto;

import br.com.fiap.esgresiduos.entity.PontoColeta;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de saída com os dados de um Ponto de Coleta.
 * Inclui o percentual de ocupação calculado para exibição no front-end.
 */
public record PontoColetaResponse(

    Long id,
    String nome,
    String endereco,
    BigDecimal latitude,
    BigDecimal longitude,
    PontoColeta.TipoResiduo tipoResiduo,
    BigDecimal capacidadeMaximaKg,
    BigDecimal volumeAtualKg,

    /** Percentual de ocupação calculado: (volumeAtual / capacidadeMaxima) * 100 */
    BigDecimal percentualOcupacao,

    Integer percentualAlerta,
    PontoColeta.StatusPontoColeta status,
    LocalDateTime dataCadastro

) {
    /**
     * Método fábrica estático que converte uma entidade {@link PontoColeta}
     * em um {@link PontoColetaResponse}, calculando o percentual de ocupação.
     */
    public static PontoColetaResponse fromEntity(PontoColeta ponto) {
        BigDecimal percentualOcupacao = BigDecimal.ZERO;

        if (ponto.getCapacidadeMaximaKg() != null
                && ponto.getCapacidadeMaximaKg().compareTo(BigDecimal.ZERO) > 0) {
            percentualOcupacao = ponto.getVolumeAtualKg()
                .multiply(BigDecimal.valueOf(100))
                .divide(ponto.getCapacidadeMaximaKg(), 2, java.math.RoundingMode.HALF_UP);
        }

        return new PontoColetaResponse(
            ponto.getId(),
            ponto.getNome(),
            ponto.getEndereco(),
            ponto.getLatitude(),
            ponto.getLongitude(),
            ponto.getTipoResiduo(),
            ponto.getCapacidadeMaximaKg(),
            ponto.getVolumeAtualKg(),
            percentualOcupacao,
            ponto.getPercentualAlerta(),
            ponto.getStatus(),
            ponto.getDataCadastro()
        );
    }
}
