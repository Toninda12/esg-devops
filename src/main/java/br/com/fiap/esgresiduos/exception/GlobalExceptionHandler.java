package br.com.fiap.esgresiduos.exception;

import br.com.fiap.esgresiduos.dto.ErroResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tratamento centralizado de exceções para toda a API.
 * <p>
 * Intercepta exceções lançadas nas camadas de Controller e Service,
 * retornando uma resposta padronizada ({@link ErroResponse}) com o
 * código HTTP correto.
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Trata entidades não encontradas → 404 Not Found.
     */
    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> handleEntidadeNaoEncontrada(
            EntidadeNaoEncontradaException ex) {

        ErroResponse erro = new ErroResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            List.of(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    /**
     * Trata violações de regra de negócio → 422 Unprocessable Entity.
     */
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> handleRegraNegocio(RegraNegocioException ex) {

        ErroResponse erro = new ErroResponse(
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            ex.getMessage(),
            List.of(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erro);
    }

    /**
     * Trata falhas de validação do Bean Validation (@Valid) → 400 Bad Request.
     * Retorna a lista de todos os campos que falharam na validação.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidacao(MethodArgumentNotValidException ex) {

        List<ErroResponse.ErroCampo> errosCampos = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fe -> new ErroResponse.ErroCampo(fe.getField(), fe.getDefaultMessage()))
            .toList();

        ErroResponse erro = new ErroResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Dados de entrada inválidos. Corrija os campos e tente novamente.",
            errosCampos,
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    /**
     * Trata qualquer outra exceção não prevista → 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleErroGenerico(Exception ex) {

        logger.error("Erro interno não tratado na API ESG Resíduos", ex);

        ErroResponse erro = new ErroResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno no servidor. Tente novamente mais tarde.",
                List.of(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(erro);
    }
}
