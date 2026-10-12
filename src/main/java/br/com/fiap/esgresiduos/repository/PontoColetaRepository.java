package br.com.fiap.esgresiduos.repository;

import br.com.fiap.esgresiduos.entity.PontoColeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório de acesso a dados da entidade {@link PontoColeta}.
 */
@Repository
public interface PontoColetaRepository extends JpaRepository<PontoColeta, Long> {

    /** Lista pontos de coleta pelo tipo de resíduo aceito. */
    List<PontoColeta> findByTipoResiduo(PontoColeta.TipoResiduo tipoResiduo);

    /** Lista pontos de coleta pelo status atual. */
    List<PontoColeta> findByStatus(PontoColeta.StatusPontoColeta status);

    /**
     * Busca todos os pontos ativos que já atingiram ou ultrapassaram
     * o percentual de alerta configurado.
     * Útil para varredura periódica de alertas pendentes.
     */
    @Query("""
        SELECT p FROM PontoColeta p
        WHERE p.status = 'ATIVO'
          AND (p.volumeAtualKg * 100 / p.capacidadeMaximaKg) >= p.percentualAlerta
        """)
    List<PontoColeta> findPontosComLimiteAtingido();
}
