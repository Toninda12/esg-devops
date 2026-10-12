package br.com.fiap.esgresiduos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa um Ponto de Coleta Seletiva.
 * Contém informações de localização, tipo de resíduo aceito,
 * capacidade total e volume já ocupado.
 * <p>
 * A lógica de alerta automático é acionada quando
 * {@code volumeAtualKg} ultrapassa o {@code percentualAlerta}
 * aplicado à {@code capacidadeMaximaKg}.
 */
@Entity
@Table(name = "TB_PONTO_COLETA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class PontoColeta {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "seq_ponto_coleta"
    )
    @SequenceGenerator(
        name = "seq_ponto_coleta",
        sequenceName = "SEQ_PONTO_COLETA",
        allocationSize = 1
    )
    @Column(name = "ID_PONTO_COLETA")
    private Long id;

    @Column(name = "NM_PONTO_COLETA", nullable = false, length = 200)
    private String nome;

    @Column(name = "DS_ENDERECO", nullable = false, length = 300)
    private String endereco;

    @Column(name = "NR_LATITUDE", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "NR_LONGITUDE", precision = 10, scale = 7)
    private BigDecimal longitude;

    /**
     * Tipo de resíduo que o ponto aceita: PLASTICO, PAPEL, VIDRO, METAL, ORGANICO, ELETRONICO, GERAL.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_RESIDUO", nullable = false, length = 30)
    private TipoResiduo tipoResiduo;

    /** Capacidade máxima do ponto em quilogramas. */
    @Column(name = "NR_CAPACIDADE_MAX_KG", nullable = false, precision = 10, scale = 2)
    private BigDecimal capacidadeMaximaKg;

    /** Volume atualmente ocupado no ponto, em quilogramas. */
    @Column(name = "NR_VOLUME_ATUAL_KG", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal volumeAtualKg = BigDecimal.ZERO;

    /**
     * Percentual (0–100) a partir do qual um alerta deve ser gerado.
     * Padrão: 80%.
     */
    @Column(name = "NR_PERCENTUAL_ALERTA", nullable = false)
    @Builder.Default
    private Integer percentualAlerta = 80;

    @Enumerated(EnumType.STRING)
    @Column(name = "ST_PONTO_COLETA", nullable = false, length = 20)
    @Builder.Default
    private StatusPontoColeta status = StatusPontoColeta.ATIVO;

    @Column(name = "DT_CADASTRO", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @OneToMany(mappedBy = "pontoColeta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Coleta> coletas = new ArrayList<>();

    @OneToMany(mappedBy = "pontoColeta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Alerta> alertas = new ArrayList<>();

    @PrePersist
    protected void prePersist() {
        this.dataCadastro = LocalDateTime.now();
    }

    /**
     * Verifica se o volume atual atingiu ou ultrapassou o percentual de alerta.
     *
     * @return {@code true} se o limite de alerta foi atingido
     */
    public boolean limiteAlertaAtingido() {
        if (capacidadeMaximaKg == null || capacidadeMaximaKg.compareTo(BigDecimal.ZERO) == 0) {
            return false;
        }
        BigDecimal percentualOcupado = volumeAtualKg
            .multiply(BigDecimal.valueOf(100))
            .divide(capacidadeMaximaKg, 2, java.math.RoundingMode.HALF_UP);

        return percentualOcupado.compareTo(BigDecimal.valueOf(percentualAlerta)) >= 0;
    }

    public enum TipoResiduo {
        PLASTICO, PAPEL, VIDRO, METAL, ORGANICO, ELETRONICO, GERAL
    }

    public enum StatusPontoColeta {
        ATIVO, INATIVO, CHEIO
    }
}
