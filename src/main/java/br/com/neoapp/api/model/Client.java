package br.com.neoapp.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa um cliente no sistema.
 * <p>
 * Esta classe é mapeada para a tabela {@code client_table} no banco de dados e contém
 * todas as informações cadastrais do cliente, bem como suas credenciais de acesso
 * e papéis de autorização.
 */
@Entity
@Table(name = "client_table")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Client {

    /**
     * O identificador único do cliente, gerado automaticamente como um UUID.
     * Chave primária da tabela.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * O nome completo do cliente.
     */
    @NotBlank(message = "O nome não pode ser vazio.")
    @Size(min = 2, max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * A data de nascimento do cliente.
     */
    @Past(message = "A data de nascimento deve ser no passado.")
    @Column(nullable = false)
    private LocalDate birthday;

    /**
     * O endereço de e-mail do cliente, utilizado para login e comunicação.
     * É um campo único.
     */
    @NotBlank
    @Email(message = "Formato de e-mail inválido.")
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * A senha do cliente, armazenada em formato criptografado (hash).
     */
    @NotBlank
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
    @Column(nullable = false)
    private String password;

    /**
     * O número de telefone de contato do cliente (opcional).
     */
    @Column(length = 20)
    private String phone;

    /**
     * O Cadastro de Pessoas Físicas (CPF) do cliente.
     * É um campo único.
     */
    @NotBlank
    @CPF(message = "CPF inválido.")
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    /**
     * A data e hora em que o registro do cliente foi criado.
     * Gerado automaticamente na inserção.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime creatAt;

    /**
     * A data e hora da última atualização no registro do cliente.
     * Gerado automaticamente em cada atualização.
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updateAt;

    /**
     * A lista de papéis (roles) de autorização associados ao cliente.
     * Define o nível de acesso do cliente no sistema.
     * O carregamento é EAGER para que os papéis estejam sempre disponíveis
     * junto com os dados do cliente.
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name="users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id"))
    private List<Role> roles = new ArrayList<>();

}
