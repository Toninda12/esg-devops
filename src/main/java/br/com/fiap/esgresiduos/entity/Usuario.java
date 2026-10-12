package br.com.fiap.esgresiduos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidade que representa um usuário do sistema.
 * Pode ser um morador, catador ou administrador de um ponto de coleta.
 */
@Entity
@Table(name = "TB_USUARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Usuario {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "seq_usuario"
    )
    @SequenceGenerator(
        name = "seq_usuario",
        sequenceName = "SEQ_USUARIO",
        allocationSize = 1
    )
    @Column(name = "ID_USUARIO")
    private Long id;

    @Column(name = "NM_USUARIO", nullable = false, length = 150)
    private String nome;

    @Column(name = "DS_EMAIL", nullable = false, unique = true, length = 200)
    private String email;

    /**
     * Senha armazenada já com hash (BCrypt).
     * A configuração do encoder fica com a Pessoa 3 (Security).
     */
    @Column(name = "DS_SENHA", nullable = false, length = 255)
    private String senha;

    @Column(name = "NR_TELEFONE", length = 20)
    private String telefone;

    /**
     * Perfil do usuário: MORADOR, CATADOR, ADMINISTRADOR.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "TP_PERFIL", nullable = false, length = 30)
    private PerfilUsuario perfil;

    @Column(name = "DT_CADASTRO", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @PrePersist
    protected void prePersist() {
        this.dataCadastro = LocalDateTime.now();
    }

    public enum PerfilUsuario {
        MORADOR, CATADOR, ADMINISTRADOR
    }
}
