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

@Entity
@Table(name = "client_table")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "O nome não pode ser vazio.")
    @Size(min = 2, max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @Past(message = "A data de nascimento deve ser no passado.")
    @Column(nullable = false)
    private LocalDate birthday;

    @NotBlank
    @Email(message = "Formato de e-mail inválido.")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
    @Column(nullable = false)
    private String password;

    @Column(length = 20)
    private String phone;

    @NotBlank
    @CPF(message = "CPF inválido.")
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime creatAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updateAt;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name="users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id"))
    private List<Role> roles = new ArrayList<>();

}
