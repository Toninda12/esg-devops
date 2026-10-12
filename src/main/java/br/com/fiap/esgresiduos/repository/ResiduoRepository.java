// ============================================================
// ARQUIVO: repository/ResiduoRepository.java
// ============================================================
package br.com.fiap.esgresiduos.repository;

import br.com.fiap.esgresiduos.entity.PontoColeta;
import br.com.fiap.esgresiduos.entity.Residuo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório de acesso a dados da entidade {@link Residuo}.
 */
@Repository
public interface ResiduoRepository extends JpaRepository<Residuo, Long> {

    /** Lista resíduos pelo tipo (PLASTICO, PAPEL, VIDRO, etc.). */
    List<Residuo> findByTipo(PontoColeta.TipoResiduo tipo);

    /** Lista apenas os resíduos classificados como perigosos. */
    List<Residuo> findByPerigoso(Boolean perigoso);
}
