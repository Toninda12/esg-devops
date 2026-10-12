package br.com.fiap.esgresiduos.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de saída padronizado para respostas de erro da API.
 * Retornado pelo {@code GlobalExceptionHandler} em todas as situações de erro.
 */
public record ErroResponse(

    /** Código HTTP do erro (ex.: 400, 404, 422). */
    int status,

    /** Mensagem principal do erro. */
    String mensagem,

    /**
     * Lista de erros de validação de campos (preenchida apenas quando
     * o erro for originado de uma {@code MethodArgumentNotValidException}).
     */
    List<ErroCampo> errosCampos,

    /** Timestamp do momento em que o erro ocorreu. */
    LocalDateTime timestamp

) {
    /**
     * Construtor auxiliar para erros sem lista de campos.
     */
    public ErroResponse(int status, String mensagem) {
        this(status, mensagem, List.of(), LocalDateTime.now());
    }

    /**
     * Representa um erro de validação de um campo específico.
     *
     * @param campo   Nome do campo que falhou na validação.
     * @param mensagem Mensagem de erro da anotação de validação.
     */
    public record ErroCampo(String campo, String mensagem) {}
}
