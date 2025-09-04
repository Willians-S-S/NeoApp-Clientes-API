package br.com.neoapp.api.repository;

import br.com.neoapp.api.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repositório para a entidade {@link Client}, responsável por todas as operações de
 * acesso a dados.
 * <p>
 * Esta interface utiliza o Spring Data JPA para fornecer implementações automáticas
 * de métodos de CRUD e a capacidade de definir queries customizadas.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    /**
     * Verifica se um cliente com o e-mail especificado já existe na base de dados.
     *
     * @param email O e-mail a ser verificado.
     * @return {@code true} se o e-mail já existir, {@code false} caso contrário.
     */
    boolean existsByEmail(String email);

    /**
     * Verifica se um cliente com o CPF especificado já existe na base de dados.
     *
     * @param cpf O CPF a ser verificado.
     * @return {@code true} se o CPF já existir, {@code false} caso contrário.
     */
    boolean existsByCpf(String cpf);

    /**
     * Realiza uma busca paginada e dinâmica por clientes com base em múltiplos atributos.
     * <p>
     * A query utiliza a cláusula LIKE para buscas parciais em nome e e-mail.
     * Todos os parâmetros de filtro são opcionais; se um parâmetro for nulo,
     * ele será ignorado na consulta.
     *
     * @param name           Parte do nome do cliente para a busca (case-insensitive).
     * @param email          Parte do e-mail do cliente para a busca (case-insensitive).
     * @param cpf            O CPF exato do cliente.
     * @param phone          Parte do telefone do cliente.
     * @param birthdayStart  A data de início do intervalo de nascimento (inclusivo).
     * @param birthdayEnd    A data de fim do intervalo de nascimento (inclusivo).
     * @param pageable       O objeto de paginação e ordenação.
     * @return uma {@link Page} de clientes que correspondem aos critérios de busca.
     */
    @Query(value = "SELECT * FROM client_table c WHERE " +
            "(:name IS NULL OR :name = '' OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:email IS NULL OR :email = '' OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(:cpf IS NULL OR :cpf = '' OR c.cpf = :cpf) AND " +
            "(:phone IS NULL OR :phone = '' OR c.phone LIKE CONCAT('%', :phone, '%')) AND " +
            "(:birthdayStart IS NULL OR c.birthday >= :birthdayStart) AND " +
            "(:birthdayEnd IS NULL OR c.birthday <= :birthdayEnd)",
            nativeQuery = true
    )
    Page<Client> getAllClientsWithAttributesPage(String name, String email, String cpf, String phone, LocalDate birthdayStart, LocalDate birthdayEnd, Pageable pageable);
    /**
     * Busca um único cliente com base em uma combinação de atributos exatos.
     * <p>
     * Todos os parâmetros de filtro são opcionais; se um parâmetro for nulo,
     * ele será ignorado na consulta.
     *
     * @param name      O nome exato do cliente.
     * @param email     O e-mail exato do cliente.
     * @param cpf       O CPF exato do cliente.
     * @param phone     O telefone exato do cliente.
     * @param birthday  A data de nascimento exata do cliente.
     * @return um {@link Optional} contendo o cliente encontrado, ou vazio se nenhum corresponder.
     */
    @Query("SELECT c FROM Client c WHERE " +
            "(:name IS NULL OR c.name = :name) AND " +
            "(:email IS NULL OR c.email = :email) AND " +
            "(:cpf IS NULL OR c.cpf = :cpf) AND " +
            "(:phone IS NULL OR c.phone = :phone) AND " +
            "(:birthday IS NULL OR c.birthday = :birthday)"
    )
    Optional<Client> findClientsWithAttributes(String name, String email, String cpf, String phone, LocalDate birthday);

    /**
     * Busca um cliente pelo seu endereço de e-mail exato.
     *
     * @param email O e-mail a ser buscado.
     * @return um {@link Optional} contendo o cliente, ou vazio se não for encontrado.
     */
    Optional<Client> findByEmail(String email);
}