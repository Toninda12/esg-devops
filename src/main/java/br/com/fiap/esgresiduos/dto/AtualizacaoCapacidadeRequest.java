package br.com.fiap.esgresiduos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO de entrada para atualização do volume atual de um Ponto de Coleta.
 * Utilizado pelo endpoint PUT /pontos-coleta/{id}/capacidade.
 */
public record AtualizacaoCapacidadeRequest(

    @NotNull(message = "O novo volume atual em kg é obrigatório.")
    @DecimalMin(value = "0.0", inclusive = true, message = "O volume não pode ser negativo.")
    BigDecimal novoVolumeAtualKg

) {}
