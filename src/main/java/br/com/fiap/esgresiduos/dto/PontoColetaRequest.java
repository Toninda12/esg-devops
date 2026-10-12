// ============================================================
// ARQUIVO: dto/PontoColetaRequest.java
// ============================================================
package br.com.fiap.esgresiduos.dto;

import br.com.fiap.esgresiduos.entity.PontoColeta;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO de entrada para criação e atualização de um Ponto de Coleta.
 */
public record PontoColetaRequest(

    @NotBlank(message = "O nome do ponto de coleta é obrigatório.")
    @Size(max = 200, message = "O nome não pode ultrapassar 200 caracteres.")
    String nome,

    @NotBlank(message = "O endereço é obrigatório.")
    @Size(max = 300, message = "O endereço não pode ultrapassar 300 caracteres.")
    String endereco,

    @DecimalMin(value = "-90.0", message = "Latitude inválida.")
    @DecimalMax(value = "90.0",  message = "Latitude inválida.")
    BigDecimal latitude,

    @DecimalMin(value = "-180.0", message = "Longitude inválida.")
    @DecimalMax(value = "180.0",  message = "Longitude inválida.")
    BigDecimal longitude,

    @NotNull(message = "O tipo de resíduo é obrigatório.")
    PontoColeta.TipoResiduo tipoResiduo,

    @NotNull(message = "A capacidade máxima é obrigatória.")
    @DecimalMin(value = "0.01", message = "A capacidade máxima deve ser maior que zero.")
    BigDecimal capacidadeMaximaKg,

    @Min(value = 1,   message = "O percentual de alerta deve ser entre 1 e 100.")
    @Max(value = 100, message = "O percentual de alerta deve ser entre 1 e 100.")
    Integer percentualAlerta

) {}
