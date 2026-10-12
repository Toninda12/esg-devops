// ============================================================
// ARQUIVO: dto/ColetaRequest.java
// ============================================================
package br.com.fiap.esgresiduos.dto;

import br.com.fiap.esgresiduos.entity.Coleta;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO de entrada para registro de uma nova Coleta.
 */
public record ColetaRequest(

    @NotNull(message = "O ID do ponto de coleta é obrigatório.")
    Long idPontoColeta,

    @NotNull(message = "O ID do usuário é obrigatório.")
    Long idUsuario,

    // idResiduo é opcional — pode não ser especificado no momento do descarte
    Long idResiduo,

    @NotNull(message = "A quantidade em kg é obrigatória.")
    @DecimalMin(value = "0.001", message = "A quantidade deve ser maior que zero.")
    BigDecimal quantidadeKg,

    @Size(max = 500, message = "A observação não pode ultrapassar 500 caracteres.")
    String observacao,

    @NotNull(message = "O tipo de operação é obrigatório (DESCARTE ou RETIRADA).")
    Coleta.TipoOperacao tipoOperacao

) {}
