// ============================================================
// ARQUIVO: repository/UsuarioRepository.java
// ============================================================
package br.com.fiap.esgresiduos.repository;

import br.com.fiap.esgresiduos.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório de acesso a dados da entidade {@link Usuario}.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca um usuário pelo endereço de e-mail.
     * Utilizado no processo de autenticação (Pessoa 3 — Security).
     */
    Optional<Usuario> findByEmail(String email);

    /** Verifica se já existe um usuário cadastrado com o e-mail informado. */
    boolean existsByEmail(String email);
}
