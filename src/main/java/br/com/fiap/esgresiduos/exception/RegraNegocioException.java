// ============================================================
// ARQUIVO: exception/RegraNegocioException.java
// ============================================================
package br.com.fiap.esgresiduos.exception;

/**
 * Exceção lançada quando uma operação viola uma regra de negócio.
 * Ex.: tentar descartar mais kg do que a capacidade restante de um ponto.
 * Resulta em uma resposta HTTP 422 Unprocessable Entity.
 */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
