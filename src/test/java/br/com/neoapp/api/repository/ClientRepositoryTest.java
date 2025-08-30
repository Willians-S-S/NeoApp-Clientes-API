package br.com.neoapp.api.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ClientRepositoryTest {
    @Autowired
    ClientRepository clientRepository;

    @Test
    void shouldReturnTrueWhenEmailAlreadyExists(){
        String emailExist = "ana.silva@example.com";

        boolean exist = clientRepository.existsByEmail(emailExist);

        Assertions.assertTrue(exist);
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist(){
        String emailNonExist = "naoexist.silva@example.com";

        boolean nonExist = clientRepository.existsByEmail(emailNonExist);

        Assertions.assertFalse(nonExist);
    }

    @Test
    void shouldReturnTrueWhenCpfAlreadyExists(){
        String cpfExist = "11122233344";

        boolean exist = clientRepository.existsByCpf(cpfExist);

        Assertions.assertTrue(exist);
    }

    @Test
    void shouldReturnFalseWhenCpfDoesNotExist(){
        String cpfNonExist = "11111111111";

        boolean nonExist = clientRepository.existsByCpf(cpfNonExist);

        Assertions.assertFalse(nonExist);
    }
}
