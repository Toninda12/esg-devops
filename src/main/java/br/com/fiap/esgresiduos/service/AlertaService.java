package br.com.fiap.esgresiduos.service;

import br.com.fiap.esgresiduos.dto.AlertaResponse;
import br.com.fiap.esgresiduos.entity.Alerta;
import br.com.fiap.esgresiduos.entity.PontoColeta;
import br.com.fiap.esgresiduos.exception.EntidadeNaoEncontradaException;
import br.com.fiap.esgresiduos.repository.AlertaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço responsável pela geração e gestão dos Alertas do sistema.
 * <p>
 * A principal responsabilidade deste serviço é verificar, após cada
 * atualização de volume em um {@link PontoColeta}, se o limite
 * configurado foi atingido e, em caso positivo, gerar um alerta automático.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository alertaRepository;

    // -------------------------------------------------------------------------
    // LEITURA
    // -------------------------------------------------------------------------

    /** Retorna todos os alertas cadastrados no sistema. */
    @Transactional(readOnly = true)
    public List<AlertaResponse> listarTodos() {
        return alertaRepository.findAll()
            .stream()
            .map(AlertaResponse::fromEntity)
            .toList();
    }

    /** Retorna todos os alertas com status PENDENTE. */
    @Transactional(readOnly = true)
    public List<AlertaResponse> listarPendentes() {
        return alertaRepository.findByStatus(Alerta.StatusAlerta.PENDENTE)
            .stream()
            .map(AlertaResponse::fromEntity)
            .toList();
    }

    /** Retorna os alertas de um Ponto de Coleta específico. */
    @Transactional(readOnly = true)
    public List<AlertaResponse> listarPorPontoColeta(Long idPontoColeta) {
        return alertaRepository.findByPontoColetaId(idPontoColeta)
            .stream()
            .map(AlertaResponse::fromEntity)
            .toList();
    }

    // -------------------------------------------------------------------------
    // GERAÇÃO AUTOMÁTICA DE ALERTAS (chamado pelo PontoColetaService)
    // -------------------------------------------------------------------------

    /**
     * Verifica se o {@link PontoColeta} atingiu o limite de alerta e,
     * caso positivo, persiste um novo {@link Alerta} automaticamente.
     * <p>
     * Para evitar alertas duplicados, o método não cria um novo alerta
     * se já existir um com status PENDENTE para o mesmo ponto.
     *
     * @param ponto O ponto de coleta que acabou de ter seu volume atualizado.
     */
    @Transactional
    public void verificarEGerarAlerta(PontoColeta ponto) {
        if (!ponto.limiteAlertaAtingido()) {
            return; // Limite não atingido — nenhuma ação necessária
        }

        boolean alertaPendenteJaExiste = alertaRepository.existsByPontoColetaIdAndStatus(
            ponto.getId(), Alerta.StatusAlerta.PENDENTE
        );

        if (alertaPendenteJaExiste) {
            log.info("Alerta pendente já existe para o ponto de coleta ID={}. Nenhum novo alerta gerado.",
                ponto.getId());
            return;
        }

        BigDecimal percentualOcupado = calcularPercentualOcupacao(ponto);
        Alerta.TipoAlerta tipo = definirTipoAlerta(percentualOcupado);
        String mensagem = construirMensagemAlerta(ponto, percentualOcupado);

        Alerta alerta = Alerta.builder()
            .pontoColeta(ponto)
            .mensagem(mensagem)
            .percentualOcupacao(percentualOcupado.intValue())
            .tipoAlerta(tipo)
            .status(Alerta.StatusAlerta.PENDENTE)
            .build();

        alertaRepository.save(alerta);

        log.info("ALERTA GERADO — Ponto: '{}' | Ocupação: {}% | Tipo: {}",
            ponto.getNome(), percentualOcupado, tipo);
    }

    /**
     * Marca um alerta como RESOLVIDO.
     *
     * @param idAlerta ID do alerta a ser resolvido.
     * @return O alerta atualizado.
     */
    @Transactional
    public AlertaResponse resolverAlerta(Long idAlerta) {
        Alerta alerta = alertaRepository.findById(idAlerta)
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Alerta", idAlerta));

        alerta.setStatus(Alerta.StatusAlerta.RESOLVIDO);
        alerta.setDataResolucao(LocalDateTime.now());

        return AlertaResponse.fromEntity(alertaRepository.save(alerta));
    }

    // -------------------------------------------------------------------------
    // MÉTODOS PRIVADOS AUXILIARES
    // -------------------------------------------------------------------------

    private BigDecimal calcularPercentualOcupacao(PontoColeta ponto) {
        return ponto.getVolumeAtualKg()
            .multiply(BigDecimal.valueOf(100))
            .divide(ponto.getCapacidadeMaximaKg(), 0, RoundingMode.HALF_UP);
    }

    private Alerta.TipoAlerta definirTipoAlerta(BigDecimal percentualOcupado) {
        if (percentualOcupado.compareTo(BigDecimal.valueOf(100)) >= 0) {
            return Alerta.TipoAlerta.CAPACIDADE_MAXIMA;
        }
        return Alerta.TipoAlerta.CAPACIDADE_CRITICA;
    }

    private String construirMensagemAlerta(PontoColeta ponto, BigDecimal percentualOcupado) {
        return ("Ponto de Coleta '%s' atingiu %s%% de sua capacidade máxima (%.2f kg / %.2f kg). "
                + "Coleta necessária urgente!")
            .formatted(
                ponto.getNome(),
                percentualOcupado.toPlainString(),
                ponto.getVolumeAtualKg(),
                ponto.getCapacidadeMaximaKg()
            );
    }
}
