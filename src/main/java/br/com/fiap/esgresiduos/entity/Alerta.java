package br.com.fiap.esgresiduos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidade que representa um Alerta gerado automaticamente pelo sistema.
 * Um alerta é criado quando o volume de um Ponto de Coleta atinge
 * o percentual configurado de sua capacidade máxima.
 */
@Entity
@Table(name = "TB_ALERTA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Alerta {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "seq_alerta"
    )
    @SequenceGenerator(
        name = "seq_alerta",
        sequenceName = "SEQ_ALERTA",
        allocationSize = 1
    )
    @Column(name = "ID_ALERTA")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PONTO_COLETA", nullable = false)
    private PontoColeta pontoColeta;

    /**
     * Mensagem descritiva do alerta gerado.
     * Ex.: "Ponto de Coleta 'Ecoponto Centro' atingiu 85% de sua capacidade."
     */
    @Column(name = "DS_MENSAGEM", nullable = false, length = 500)
    private String mensagem;

    /** Percentual de ocupação no momento em que o alerta foi gerado. */
    @Column(name = "NR_PERCENTUAL_OCUPACAO", nullable = false)
    private Integer percentualOcupacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "TP_ALERTA", nullable = false, length = 30)
    private TipoAlerta tipoAlerta;

    @Enumerated(EnumType.STRING)
    @Column(name = "ST_ALERTA", nullable = false, length = 20)
    @Builder.Default
    private StatusAlerta status = StatusAlerta.PENDENTE;

    @Column(name = "DT_ALERTA", nullable = false, updatable = false)
    private LocalDateTime dataAlerta;

    /** Data em que o alerta foi resolvido (ponto esvaziado ou retirada realizada). */
    @Column(name = "DT_RESOLUCAO")
    private LocalDateTime dataResolucao;

    @PrePersist
    protected void prePersist() {
        this.dataAlerta = LocalDateTime.now();
    }

    public enum TipoAlerta {
        CAPACIDADE_CRITICA,   // >= percentual configurado (ex.: 80%)
        CAPACIDADE_MAXIMA,    // 100% cheio
        COLETA_NECESSARIA     // Alerta manual solicitando coleta
    }

    public enum StatusAlerta {
        PENDENTE, VISUALIZADO, RESOLVIDO
    }
}
