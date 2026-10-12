package br.com.fiap.esgresiduos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidade que representa um tipo de Resíduo cadastrado no sistema.
 * Contém instruções de descarte correto e destinação ambiental adequada,
 * servindo como base para as notificações enviadas aos usuários.
 */
@Entity
@Table(name = "TB_RESIDUO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Residuo {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "seq_residuo"
    )
    @SequenceGenerator(
        name = "seq_residuo",
        sequenceName = "SEQ_RESIDUO",
        allocationSize = 1
    )
    @Column(name = "ID_RESIDUO")
    private Long id;

    @Column(name = "NM_RESIDUO", nullable = false, length = 150)
    private String nome;

    /** Descrição detalhada do tipo de resíduo. */
    @Column(name = "DS_RESIDUO", length = 500)
    private String descricao;

    /**
     * Instrução de descarte correto exibida ao usuário.
     * Ex.: "Lave antes de descartar. Retire tampas e rótulos."
     */
    @Column(name = "DS_INSTRUCAO_DESCARTE", length = 1000)
    private String instrucaoDescarte;

    /** Destinação ambiental adequada. Ex.: "Reciclagem, Compostagem, Aterro Controlado." */
    @Column(name = "DS_DESTINACAO", length = 300)
    private String destinacaoAdequada;

    @Enumerated(EnumType.STRING)
    @Column(name = "TP_RESIDUO", nullable = false, length = 30)
    private PontoColeta.TipoResiduo tipo;

    /** Indica se o resíduo é perigoso (requer descarte especial). */
    @Column(name = "FL_PERIGOSO", nullable = false)
    @Builder.Default
    private Boolean perigoso = false;

    @Column(name = "DT_CADASTRO", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @PrePersist
    protected void prePersist() {
        this.dataCadastro = LocalDateTime.now();
    }
}
