package br.com.neoapp.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

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
    private String name;
    private LocalDate birthday;
    private String email;
    private String phone;
    private String cpf;
    private OffsetDateTime creatAt;
    private OffsetDateTime updateAt;
}
