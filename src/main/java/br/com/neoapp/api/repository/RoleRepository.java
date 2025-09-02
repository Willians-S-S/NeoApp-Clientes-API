package br.com.neoapp.api.repository;

import br.com.neoapp.api.enums.RoleName;
import br.com.neoapp.api.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(RoleName name);
}
