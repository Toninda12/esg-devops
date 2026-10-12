// ============================================================
// ARQUIVO: exception/EntidadeNaoEncontradaException.java
// ============================================================
package br.com.fiap.esgresiduos.exception;

/**
 * Exceção lançada quando uma entidade solicitada não é encontrada no banco de dados.
 * Resulta em uma resposta HTTP 404 Not Found.
 */
public class EntidadeNaoEncontradaException extends RuntimeException {

    public EntidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }

    public EntidadeNaoEncontradaException(String entidade, Long id) {
        super("%s com ID %d não encontrado(a).".formatted(entidade, id));
    }
}
