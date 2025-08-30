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
}
