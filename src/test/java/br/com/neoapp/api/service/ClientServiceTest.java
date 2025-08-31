package br.com.neoapp.api.service;

import br.com.neoapp.api.controller.dto.ClientRequestDTO;
import br.com.neoapp.api.controller.dto.ClientResponseDTO;
import br.com.neoapp.api.exceptions.CpfExistsException;
import br.com.neoapp.api.exceptions.EmailExistsException;
import br.com.neoapp.api.mapper.ClientMapper;
import br.com.neoapp.api.model.Client;
import br.com.neoapp.api.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários para o Serviço de Cliente (ClientService)")
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
    @DisplayName("Deve criar um cliente com sucesso ao fornecer dados válidos")
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
    @DisplayName("Deve lançar uma exceção ao tentar criar um cliente com e-mail já existente")
    void createClientShouldThrowEmailExistsException(){
        when(clientRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(EmailExistsException.class, () ->
                clientService.creatClient(clientRequestDTO));

        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar criar um cliente com CPF já existente")
    void createClientShouldThrowCpfExistsException(){
        when(clientRepository.existsByCpf(anyString())).thenReturn(true);

        assertThrows(CpfExistsException.class, () ->
                clientService.creatClient(clientRequestDTO));

        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar uma página de DTOs de cliente com sucesso")
    void getAllClientsPageable_WhenClientsExist_ShouldReturnClientPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Client client2 = new Client();
        client2.setId(UUID.randomUUID().toString());
        client2.setName("Maria");
        client2.setBirthday(LocalDate.of(1995, 5, 5));
        client2.setEmail("maria@email.com");
        client2.setPassword("senha@123");
        client2.setPhone("11988888888");
        client2.setCpf("98765432100");

        List<Client> clientList = List.of(savedClient, client2);
        Page<Client> clientPage = new PageImpl<>(clientList, pageable, clientList.size());

        int age2 = Period.between(client2.getBirthday(), LocalDate.now()).getYears();
        ClientResponseDTO dto2 = new ClientResponseDTO(
                client2.getId(),
                client2.getName(),
                age2,
                client2.getEmail(),
                client2.getPhone(),
                client2.getCpf(),
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        List<ClientResponseDTO> dtoList = List.of(clientResponseDTO, dto2);
        Page<ClientResponseDTO> expectedDtoPage = new PageImpl<>(dtoList, pageable, dtoList.size());

        when(clientRepository.findAll(pageable)).thenReturn(clientPage);
        when(clientMapper.toPageResponse(clientPage)).thenReturn(expectedDtoPage);

        Page<ClientResponseDTO> result = clientService.getAllClientsPageable(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result).isEqualTo(expectedDtoPage);

        verify(clientRepository, times(1)).findAll(pageable);
        verify(clientMapper, times(1)).toPageResponse(clientPage);
    }

}
