package br.com.fiap.esgresiduos.repository;

import br.com.fiap.esgresiduos.entity.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório de acesso a dados da entidade {@link Alerta}.
 */
@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    /** Lista todos os alertas de um Ponto de Coleta específico. */
    List<Alerta> findByPontoColetaId(Long idPontoColeta);

    /** Lista alertas pelo status (PENDENTE, VISUALIZADO, RESOLVIDO). */
    List<Alerta> findByStatus(Alerta.StatusAlerta status);

    /** Lista alertas por tipo. */
    List<Alerta> findByTipoAlerta(Alerta.TipoAlerta tipoAlerta);

    /**
     * Verifica se já existe um alerta PENDENTE para o ponto informado,
     * evitando alertas duplicados consecutivos.
     */
    boolean existsByPontoColetaIdAndStatus(Long idPontoColeta, Alerta.StatusAlerta status);
}
