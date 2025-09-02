package br.com.neoapp.api.model;

import br.com.neoapp.api.enums.RoleName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa um papel (role) de autorização no sistema.
 * <p>
 * Mapeada para a tabela {@code role_table}, esta classe é usada para definir
 * os diferentes níveis de permissão que um {@link Client} pode ter.
 */
@Entity
@Table(name = "role_table")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Role {
    /**
     * O identificador único do papel, gerado automaticamente como um UUID.
     * Chave primária da tabela.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * O nome do papel, definido pelo enum {@link RoleName}.
     * É armazenado como uma String no banco de dados (e.g., "ADMIN", "USER")
     * graças à anotação {@code @Enumerated(EnumType.STRING)}.
     */
    @Enumerated(EnumType.STRING)
    private RoleName name;
}
