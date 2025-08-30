package br.com.neoapp.api.service;

import br.com.neoapp.api.controller.dto.ClientRequestDTO;
import br.com.neoapp.api.controller.dto.ClientResponseDTO;
import br.com.neoapp.api.exceptions.CpfExistsException;
import br.com.neoapp.api.exceptions.EmailExistsException;
import br.com.neoapp.api.mapper.ClientMapper;
import br.com.neoapp.api.model.Client;
import br.com.neoapp.api.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientService clientService;

    private ClientRequestDTO clientRequestDTO;
    private  ClientResponseDTO clientResponseDTO;
    private Client client;
    private Client savedClient;

    @BeforeEach
    void setUp() {
        clientRequestDTO = new ClientRequestDTO(
                "João",
                LocalDate.of(1990, 1, 1),
                "joao@email.com",
                "password123",
                "11999999999",
                "12345678900"
        );

        client = new Client();
        client.setName(clientRequestDTO.name());
        client.setBirthday(clientRequestDTO.birthday());
        client.setEmail(clientRequestDTO.email());
        client.setPassword(clientRequestDTO.password());
        client.setPhone(clientRequestDTO.phone());
        client.setCpf(clientRequestDTO.cpf());

        savedClient = new Client();
        savedClient.setId(UUID.randomUUID().toString());
        savedClient.setName(clientRequestDTO.name());
        savedClient.setBirthday(clientRequestDTO.birthday());
        savedClient.setEmail(clientRequestDTO.email());
        savedClient.setPassword(clientRequestDTO.password());
        savedClient.setPhone(clientRequestDTO.phone());
        savedClient.setCpf(clientRequestDTO.cpf());

        int age = Period.between(savedClient.getBirthday(), LocalDate.now()).getYears();

        clientResponseDTO = new ClientResponseDTO(
                savedClient.getId(),
                savedClient.getName(),
                age,
                savedClient.getEmail(),
                savedClient.getPhone(),
                savedClient.getCpf(),
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
    }

    @Test
    void createClientWithValidDataShouldSucceed() {
        when(clientRepository.existsByEmail(anyString())).thenReturn(false);
        when(clientRepository.existsByCpf(anyString())).thenReturn(false);
        when(clientMapper.toEntity(clientRequestDTO)).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(savedClient);
        when(clientMapper.toResponse(savedClient)).thenReturn(clientResponseDTO);

        clientService.creatClient(clientRequestDTO);

        verify(clientRepository).save(client);
    }

    @Test
    void createClientShouldThrowEmailExistsException(){
        when(clientRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(EmailExistsException.class, () ->
                clientService.creatClient(clientRequestDTO));

        verify(clientRepository, never()).save(any());
    }

    @Test
    void createClientShouldThrowCpfExistsException(){
        when(clientRepository.existsByCpf(anyString())).thenReturn(true);

        assertThrows(CpfExistsException.class, () ->
                clientService.creatClient(clientRequestDTO));

        verify(clientRepository, never()).save(any());
    }
}
