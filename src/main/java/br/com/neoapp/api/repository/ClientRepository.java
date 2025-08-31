package br.com.neoapp.api.repository;

import br.com.neoapp.api.model.Client;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
}
