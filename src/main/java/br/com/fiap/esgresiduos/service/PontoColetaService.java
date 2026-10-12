package br.com.fiap.esgresiduos.service;

import br.com.fiap.esgresiduos.dto.AtualizacaoCapacidadeRequest;
import br.com.fiap.esgresiduos.dto.PontoColetaRequest;
import br.com.fiap.esgresiduos.dto.PontoColetaResponse;
import br.com.fiap.esgresiduos.entity.PontoColeta;
import br.com.fiap.esgresiduos.exception.EntidadeNaoEncontradaException;
import br.com.fiap.esgresiduos.exception.RegraNegocioException;
import br.com.fiap.esgresiduos.repository.PontoColetaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço responsável pelas regras de negócio relacionadas ao {@link PontoColeta}.
 * <p>
 * Orquestra a atualização de volume e delega ao {@link AlertaService}
 * a verificação e geração automática de alertas de capacidade.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PontoColetaService {

    private final PontoColetaRepository pontoColetaRepository;
    private final AlertaService alertaService;

    // -------------------------------------------------------------------------
    // LEITURA
    // -------------------------------------------------------------------------

    /** Retorna todos os pontos de coleta cadastrados. */
    @Transactional(readOnly = true)
    public List<PontoColetaResponse> listarTodos() {
        return pontoColetaRepository.findAll()
            .stream()
            .map(PontoColetaResponse::fromEntity)
            .toList();
    }

    /** Retorna um ponto de coleta pelo ID, ou lança exceção se não existir. */
    @Transactional(readOnly = true)
    public PontoColetaResponse buscarPorId(Long id) {
        PontoColeta ponto = buscarEntidadePorId(id);
        return PontoColetaResponse.fromEntity(ponto);
    }

    /** Retorna todos os pontos de coleta de um determinado tipo de resíduo. */
    @Transactional(readOnly = true)
    public List<PontoColetaResponse> listarPorTipoResiduo(PontoColeta.TipoResiduo tipo) {
        return pontoColetaRepository.findByTipoResiduo(tipo)
            .stream()
            .map(PontoColetaResponse::fromEntity)
            .toList();
    }

    // -------------------------------------------------------------------------
    // ESCRITA
    // -------------------------------------------------------------------------

    /**
     * Cadastra um novo Ponto de Coleta.
     *
     * @param request Dados do ponto a ser criado.
     * @return O ponto criado com ID gerado.
     */
    @Transactional
    public PontoColetaResponse criar(PontoColetaRequest request) {
        PontoColeta ponto = PontoColeta.builder()
            .nome(request.nome())
            .endereco(request.endereco())
            .latitude(request.latitude())
            .longitude(request.longitude())
            .tipoResiduo(request.tipoResiduo())
            .capacidadeMaximaKg(request.capacidadeMaximaKg())
            .percentualAlerta(request.percentualAlerta() != null ? request.percentualAlerta() : 80)
            .build();

        PontoColeta pontoPersistido = pontoColetaRepository.save(ponto);
        log.info("Ponto de coleta criado com sucesso. ID={}, Nome='{}'",
            pontoPersistido.getId(), pontoPersistido.getNome());

        return PontoColetaResponse.fromEntity(pontoPersistido);
    }

    /**
     * Atualiza os dados cadastrais de um Ponto de Coleta existente.
     *
     * @param id      ID do ponto a ser atualizado.
     * @param request Novos dados do ponto.
     * @return O ponto atualizado.
     */
    @Transactional
    public PontoColetaResponse atualizar(Long id, PontoColetaRequest request) {
        PontoColeta ponto = buscarEntidadePorId(id);

        ponto.setNome(request.nome());
        ponto.setEndereco(request.endereco());
        ponto.setLatitude(request.latitude());
        ponto.setLongitude(request.longitude());
        ponto.setTipoResiduo(request.tipoResiduo());
        ponto.setCapacidadeMaximaKg(request.capacidadeMaximaKg());

        if (request.percentualAlerta() != null) {
            ponto.setPercentualAlerta(request.percentualAlerta());
        }

        return PontoColetaResponse.fromEntity(pontoColetaRepository.save(ponto));
    }

    /**
     * Atualiza o volume atual (capacidade ocupada) de um Ponto de Coleta.
     * <p>
     * <b>Regra de negócio:</b> Após a atualização do volume, o sistema
     * verifica automaticamente se o limite de alerta foi atingido e,
     * se necessário, gera um alerta via {@link AlertaService}.
     * <p>
     * Se o novo volume for zero (ponto foi esvaziado após coleta),
     * o status é retornado a ATIVO.
     *
     * @param id      ID do ponto de coleta a ser atualizado.
     * @param request Novo volume em quilogramas.
     * @return O ponto com volume atualizado e percentual recalculado.
     * @throws RegraNegocioException se o novo volume ultrapassar a capacidade máxima.
     */
    @Transactional
    public PontoColetaResponse atualizarCapacidade(Long id, AtualizacaoCapacidadeRequest request) {
        PontoColeta ponto = buscarEntidadePorId(id);

        // Validação de negócio: volume não pode ultrapassar a capacidade máxima
        if (request.novoVolumeAtualKg().compareTo(ponto.getCapacidadeMaximaKg()) > 0) {
            throw new RegraNegocioException(
                "O volume informado (%.2f kg) ultrapassa a capacidade máxima do ponto (%.2f kg)."
                    .formatted(request.novoVolumeAtualKg(), ponto.getCapacidadeMaximaKg())
            );
        }

        ponto.setVolumeAtualKg(request.novoVolumeAtualKg());

        // Atualiza o status visual do ponto
        if (request.novoVolumeAtualKg().compareTo(ponto.getCapacidadeMaximaKg()) == 0) {
            ponto.setStatus(PontoColeta.StatusPontoColeta.CHEIO);
        } else if (request.novoVolumeAtualKg().signum() == 0) {
            ponto.setStatus(PontoColeta.StatusPontoColeta.ATIVO);
        }

        PontoColeta pontoAtualizado = pontoColetaRepository.save(ponto);

        // ── ALERTA AUTOMÁTICO ─────────────────────────────────────────────────
        // Delega ao AlertaService a responsabilidade de verificar e gerar alerta.
        alertaService.verificarEGerarAlerta(pontoAtualizado);
        // ─────────────────────────────────────────────────────────────────────

        log.info("Volume atualizado — Ponto ID={} | Novo volume: {} kg | Capacidade max: {} kg",
            id, pontoAtualizado.getVolumeAtualKg(), pontoAtualizado.getCapacidadeMaximaKg());

        return PontoColetaResponse.fromEntity(pontoAtualizado);
    }

    /**
     * Remove um Ponto de Coleta pelo ID.
     * Apenas pontos sem coletas associadas podem ser removidos.
     *
     * @param id ID do ponto a ser removido.
     * @throws RegraNegocioException se o ponto possuir coletas vinculadas.
     */
    @Transactional
    public void deletar(Long id) {
        PontoColeta ponto = buscarEntidadePorId(id);

        if (!ponto.getColetas().isEmpty()) {
            throw new RegraNegocioException(
                "O ponto de coleta '%s' possui coletas registradas e não pode ser removido."
                    .formatted(ponto.getNome())
            );
        }

        pontoColetaRepository.delete(ponto);
        log.info("Ponto de coleta removido. ID={}", id);
    }

    // -------------------------------------------------------------------------
    // MÉTODO AUXILIAR INTERNO
    // -------------------------------------------------------------------------

    /**
     * Busca a entidade {@link PontoColeta} pelo ID ou lança
     * {@link EntidadeNaoEncontradaException} se não existir.
     */
    PontoColeta buscarEntidadePorId(Long id) {
        return pontoColetaRepository.findById(id)
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Ponto de Coleta", id));
    }
}
