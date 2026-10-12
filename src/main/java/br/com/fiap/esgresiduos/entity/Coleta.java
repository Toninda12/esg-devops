package br.com.fiap.esgresiduos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade que representa um registro de Coleta de resíduos.
 * Cada coleta está associada a um Ponto de Coleta e a um Usuário
 * (quem realizou o descarte ou a coleta).
 * <p>
 * Ao registrar uma coleta, o volume do Ponto de Coleta é atualizado
 * e, se o limite for atingido, um Alerta é gerado automaticamente.
 */
@Entity
@Table(name = "TB_COLETA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Coleta {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "seq_coleta"
    )
    @SequenceGenerator(
        name = "seq_coleta",
        sequenceName = "SEQ_COLETA",
        allocationSize = 1
    )
    @Column(name = "ID_COLETA")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PONTO_COLETA", nullable = false)
    private PontoColeta pontoColeta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_RESIDUO")
    private Residuo residuo;

    /** Quantidade coletada / descartada em quilogramas. */
    @Column(name = "NR_QUANTIDADE_KG", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantidadeKg;

    /** Observações adicionais sobre a coleta. */
    @Column(name = "DS_OBSERVACAO", length = 500)
    private String observacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "TP_OPERACAO", nullable = false, length = 20)
    private TipoOperacao tipoOperacao;

    @Column(name = "DT_COLETA", nullable = false, updatable = false)
    private LocalDateTime dataColeta;

    @PrePersist
    protected void prePersist() {
        this.dataColeta = LocalDateTime.now();
    }

    /**
     * Tipo da operação:
     * DESCARTE — usuário descartou resíduo no ponto.
     * RETIRADA  — catador/empresa retirou o material do ponto.
     */
    public enum TipoOperacao {
        DESCARTE, RETIRADA
    }
}
