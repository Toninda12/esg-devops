package br.com.fiap.esgresiduos.repository;

import br.com.fiap.esgresiduos.entity.Coleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório de acesso a dados da entidade {@link Coleta}.
 */
@Repository
public interface ColetaRepository extends JpaRepository<Coleta, Long> {

    /** Lista todas as coletas de um determinado Ponto de Coleta. */
    List<Coleta> findByPontoColetaId(Long idPontoColeta);

    /** Lista todas as coletas realizadas por um determinado usuário. */
    List<Coleta> findByUsuarioId(Long idUsuario);

    /** Lista coletas dentro de um intervalo de datas. */
    List<Coleta> findByDataColetaBetween(LocalDateTime inicio, LocalDateTime fim);

    /** Lista coletas de um ponto dentro de um intervalo de datas. */
    List<Coleta> findByPontoColetaIdAndDataColetaBetween(
        Long idPontoColeta,
        LocalDateTime inicio,
        LocalDateTime fim
    );
}
