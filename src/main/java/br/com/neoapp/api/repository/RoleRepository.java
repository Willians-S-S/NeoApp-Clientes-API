package br.com.neoapp.api.repository;

import br.com.neoapp.api.enums.RoleName;
import br.com.neoapp.api.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório para a entidade {@link Role}, responsável pelas operações de
 * acesso a dados relacionadas aos papéis de usuário.
 * <p>
 * Utiliza o Spring Data JPA para fornecer as implementações de CRUD e
 * a capacidade de criar queries derivadas a partir do nome dos métodos.
 */
public interface RoleRepository extends JpaRepository<Role, String> {

    /**
     * Busca um papel (role) pelo seu nome, definido no enum {@link RoleName}.
     *
     * @param name O nome do papel a ser buscado (e.g., RoleName.ADMIN, RoleName.USER).
     * @return um {@link Optional} contendo o papel encontrado, ou vazio se não existir.
     */
    Optional<Role> findByName(RoleName name);
}
