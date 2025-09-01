package br.com.neoapp.api.repository;

import br.com.neoapp.api.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);

    @Query("SELECT c FROM Client c WHERE " +
            "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(:cpf IS NULL OR c.cpf = :cpf) AND " +
            "(:phone IS NULL OR c.phone LIKE CONCAT('%', :phone, '%')) AND " +
            "(:birthdayStart IS NULL OR c.birthday >= :birthdayStart) AND " +
            "(:birthdayEnd IS NULL OR c.birthday <= :birthdayEnd)"
    )
    Page<Client> getAllClientsWithAttributesPage(String name, String email, String cpf, String phone, LocalDate birthdayStart, LocalDate birthdayEnd, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE " +
            "(:name IS NULL OR c.name = :name) AND " +
            "(:email IS NULL OR c.email = :email) AND " +
            "(:cpf IS NULL OR c.cpf = :cpf) AND " +
            "(:phone IS NULL OR c.phone = :phone) AND " +
            "(:birthday IS NULL OR c.birthday = :birthday)"
    )
    Optional<Client> findClientsWithAttributes(String name, String email, String cpf, String phone, LocalDate birthday);

    Optional<Client> findByEmail(String email);
}