// ============================================================
// ARQUIVO: dto/AlertaResponse.java
// ============================================================
package br.com.fiap.esgresiduos.dto;

import br.com.fiap.esgresiduos.entity.Alerta;
import java.time.LocalDateTime;

/**
 * DTO de saída com os dados de um Alerta gerado pelo sistema.
 */
public record AlertaResponse(

    Long id,
    Long idPontoColeta,
    String nomePontoColeta,
    String mensagem,
    Integer percentualOcupacao,
    Alerta.TipoAlerta tipoAlerta,
    Alerta.StatusAlerta status,
    LocalDateTime dataAlerta,
    LocalDateTime dataResolucao

) {
    /**
     * Método fábrica estático que converte uma entidade {@link Alerta}
     * em um {@link AlertaResponse}.
     */
    public static AlertaResponse fromEntity(Alerta alerta) {
        return new AlertaResponse(
            alerta.getId(),
            alerta.getPontoColeta().getId(),
            alerta.getPontoColeta().getNome(),
            alerta.getMensagem(),
            alerta.getPercentualOcupacao(),
            alerta.getTipoAlerta(),
            alerta.getStatus(),
            alerta.getDataAlerta(),
            alerta.getDataResolucao()
        );
    }
}
