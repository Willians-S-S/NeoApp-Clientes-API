package br.com.neoapp.api.repository;

import br.com.neoapp.api.model.Client;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@ActiveProfiles("test")
@DataJpaTest
@DisplayName("Testes para o Repositório de Cliente (ClientRepository)")
public class ClientRepositoryTest {
    @Autowired
    ClientRepository clientRepository;

    @BeforeEach
    void setUp(){
        clientRepository.deleteAll();

        Client client = new Client(
                null,
                "Ana Silva",
                LocalDate.of(1990, 5, 15),
                "ana.silva@example.com",
                "senha@123",
                "89994564321",
                "90437179087",
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        clientRepository.save(client);
    }

    @Test
    @DisplayName("Deve retornar verdadeiro quando o e-mail já existe no banco de dados")
    void shouldReturnTrueWhenEmailAlreadyExists(){
        String emailExist = "ana.silva@example.com";

        boolean exist = clientRepository.existsByEmail(emailExist);

        Assertions.assertTrue(exist);
    }

    @Test
    @DisplayName("Deve retornar falso quando o e-mail não existe no banco de dados")
    void shouldReturnFalseWhenEmailDoesNotExist(){
        String emailNonExist = "naoexist.silva@example.com";

        boolean nonExist = clientRepository.existsByEmail(emailNonExist);

        Assertions.assertFalse(nonExist);
    }

    @Test
    @DisplayName("Deve retornar verdadeiro quando o CPF já existe no banco de dados")
    void shouldReturnTrueWhenCpfAlreadyExists(){
        String cpfExist = "90437179087";

        boolean exist = clientRepository.existsByCpf(cpfExist);

        Assertions.assertTrue(exist);
    }

    @Test
    @DisplayName("Deve retornar falso quando o CPF não existe no banco de dados")
    void shouldReturnFalseWhenCpfDoesNotExist(){
        String cpfNonExist = "11111111111";

        boolean nonExist = clientRepository.existsByCpf(cpfNonExist);

        Assertions.assertFalse(nonExist);
    }
}
