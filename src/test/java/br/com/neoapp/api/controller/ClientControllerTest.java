package br.com.neoapp.api.controller;

import br.com.neoapp.api.controller.dto.ClientRequestDTO;
import br.com.neoapp.api.controller.dto.ClientUpdateDTO;
import br.com.neoapp.api.model.Client;
import br.com.neoapp.api.repository.ClientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DisplayName("Testes de Integração para o Endpoint de Criação de Cliente")
public class ClientControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientRepository clientRepository;

    private ClientRequestDTO validRequestDTO;
    private ClientRequestDTO invalidRequestDTO;
    private ClientRequestDTO emailInvalidRequestDTO;
    private ClientRequestDTO nameInvalidRequestDTO;

    @BeforeEach
    void setUp() {
        clientRepository.deleteAll();

        validRequestDTO = new ClientRequestDTO(
                "João Silva",
                LocalDate.of(1990, 5, 15),
                "joao.silva@email.com",
                "senha@123",
                "11987654321",
                "94360802048"
        );

        invalidRequestDTO = new ClientRequestDTO(
                "João Silva",
                LocalDate.of(1990, 5, 15),
                "emailalte@email.com",
                "senha@123",
                "11987654321",
                "94360802048"
        );

        emailInvalidRequestDTO = new ClientRequestDTO(
                "João Silva",
                LocalDate.of(1990, 5, 15),
                "emailinvalido",
                "senha@123",
                "11987654321",
                "94360802048"
        );

        nameInvalidRequestDTO = new ClientRequestDTO(
                "J",
                LocalDate.of(1990, 5, 15),
                "emailalte@email.com",
                "senha@123",
                "11987654321",
                "94360802048"
        );
    }

    @Test
    @DisplayName("Deve criar cliente e retornar status 201 ao receber dados válidos")
    void creatClientWithValidDataShouldPersistClientAndReturn201() throws Exception {

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is("João Silva")))
                .andExpect(jsonPath("$.age", is(35))); // 2025 - 1990 = 35

        assertThat(clientRepository.findAll()).hasSize(1);
        assertThat(clientRepository.existsByEmail("joao.silva@email.com")).isTrue();
    }

    @Test
    @DisplayName("Deve retornar status 409 ao tentar criar cliente com e-mail já existente")
    void creatClientWithExistingEmailShouldReturn409() throws Exception {
        clientRepository.save(objectMapper.convertValue(validRequestDTO, br.com.neoapp.api.model.Client.class));

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("EMAIL_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("O endereço de e-mail informado já está registrado."))
                .andExpect(jsonPath("$.path").value("/api/v1/clients"));
    }

    @Test
    @DisplayName("Deve retornar status 409 ao tentar criar cliente com CPF já existente")
    void creatClientWithExistingCpfShouldReturn409() throws Exception {
        clientRepository.save(objectMapper.convertValue(validRequestDTO, br.com.neoapp.api.model.Client.class));

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("CPF_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("O CPF informado já está registrado."))
                .andExpect(jsonPath("$.path").value("/api/v1/clients"));
    }

    @Test
    @DisplayName("Deve retornar status 422 ao receber múltiplos campos inválidos")
    void creatClientWithInvalidDataShouldReturn422() throws Exception {
        invalidRequestDTO = new ClientRequestDTO("", null, "email-invalido", "123", null, "cpf-invalido");

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Dados inválidos. Verifique os erros de cada campo."))
                .andExpect(jsonPath("$.path").value("/api/v1/clients"));
    }

    @Test
    @DisplayName("Deve retornar status 422 para formato de e-mail inválido")
    void creatClientWithInvalidEmailShouldReturn422() throws Exception {

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailInvalidRequestDTO)))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Dados inválidos. Verifique os erros de cada campo."))
                .andExpect(jsonPath("$.path").value("/api/v1/clients"))
                .andExpect(jsonPath("$.errors[0].fieldName").value("email"))
                .andExpect(jsonPath("$.errors[0].message").value("Formato de e-mail inválido."));
    }

    @Test
    @DisplayName("Deve retornar status 422 para nome fora do tamanho permitido")
    void creatClientWithInvalidNameShouldReturn422() throws Exception {

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nameInvalidRequestDTO)))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Dados inválidos. Verifique os erros de cada campo."))
                .andExpect(jsonPath("$.path").value("/api/v1/clients"))
                .andExpect(jsonPath("$.errors[0].fieldName").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("size must be between 2 and 100"));
    }

    @Test
    @DisplayName("Deve retornar status 422 para nome nulo ou vazio")
    void creatClientWithInvalidNameEmptyShouldReturn422() throws Exception {
        ClientRequestDTO nameEmptyInvalidRequestDTO = new ClientRequestDTO(
                null,
                LocalDate.of(1990, 5, 15),
                "emailalte@email.com",
                "senha@123",
                "11987654321",
                "94360802048"
        );
        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nameEmptyInvalidRequestDTO)))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Dados inválidos. Verifique os erros de cada campo."))
                .andExpect(jsonPath("$.path").value("/api/v1/clients"))
                .andExpect(jsonPath("$.errors[0].fieldName").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("O nome não pode ser vazio."));
    }

    @Test
    @DisplayName("Deve retornar status 422 para CPF inválido")
    void creatClientWithInvalidCpfShouldReturn422() throws Exception {
        ClientRequestDTO nameEmptyInvalidRequestDTO = new ClientRequestDTO(
                "João",
                LocalDate.of(1990, 5, 15),
                "emailalte@email.com",
                "senha@123",
                "11987654321",
                "11111111111"
        );
        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nameEmptyInvalidRequestDTO)))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Dados inválidos. Verifique os erros de cada campo."))
                .andExpect(jsonPath("$.path").value("/api/v1/clients"))
                .andExpect(jsonPath("$.errors[0].fieldName").value("cpf"))
                .andExpect(jsonPath("$.errors[0].message").value("CPF inválido."));
    }

    @Test
    @DisplayName("Deve retornar status 422 para senha com menos de 8 caracteres")
    void creatClientWithInvalidPasswordShouldReturn422() throws Exception {
        ClientRequestDTO nameEmptyInvalidRequestDTO = new ClientRequestDTO(
                "João",
                LocalDate.of(1990, 5, 15),
                "emailalte@email.com",
                "senha",
                "11987654321",
                "94360802048"
        );
        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nameEmptyInvalidRequestDTO)))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Dados inválidos. Verifique os erros de cada campo."))
                .andExpect(jsonPath("$.path").value("/api/v1/clients"))
                .andExpect(jsonPath("$.errors[0].fieldName").value("password"))
                .andExpect(jsonPath("$.errors[0].message").value("A senha deve ter no mínimo 8 caracteres."));
    }

    @Test
    @DisplayName("Deve retornar uma página de clientes com os padrões (page=0, size=20)")
    void getAllClientsPageable_WithoutParams_ShouldReturnDefaultPage() throws Exception {

        clientRepository.deleteAll();

        for (int i = 1; i <= 25; i++) {
            Client client = new Client();
            client.setName("Cliente " + String.format("%02d", i));
            client.setEmail("cliente" + i + "@email.com");
            client.setCpf(gerarCpf());
            client.setPassword("senha@123");
            client.setBirthday(LocalDate.now().minusYears(20));
            clientRepository.save(client);
        }

        mockMvc.perform(get("/api/v1/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()", is(20)))
                .andExpect(jsonPath("$.totalElements", is(25)))
                .andExpect(jsonPath("$.totalPages", is(2)))
                .andExpect(jsonPath("$.number", is(0)));
    }

    @Test
    @DisplayName("Deve retornar uma página customizada de clientes (page=1, size=5)")
    void getAllClientsPageable_WithCustomParams_ShouldReturnCustomPage() throws Exception {
        for (int i = 1; i <= 12; i++) {
            Client client = new Client();
            client.setName("Cliente " + String.format("%02d", i));
            client.setEmail("cliente" + i + "@email.com");
            client.setCpf(gerarCpf());
            client.setPassword("senha@123");
            client.setBirthday(LocalDate.now().minusYears(20));
            clientRepository.save(client);
        }

        mockMvc.perform(get("/api/v1/clients")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(5)))
                .andExpect(jsonPath("$.totalElements", is(12)))
                .andExpect(jsonPath("$.totalPages", is(3)))
                .andExpect(jsonPath("$.number", is(1)));
    }

    @Test
    @DisplayName("Deve retornar uma lista de clientes ordenada por nome em ordem descendente")
    void getAllClientsPageable_WithSortParam_ShouldReturnSortedPage() throws Exception {
        clientRepository.save(new Client(null, "Bruno", LocalDate.now().minusYears(30), "bruno@email.com", "senha@123", null, gerarCpf(), null, null));
        clientRepository.save(new Client(null, "Ana", LocalDate.now().minusYears(30), "ana@email.com", "senha@123", null, gerarCpf(), null, null));
        clientRepository.save(new Client(null, "Carlos", LocalDate.now().minusYears(30), "carlos@email.com", "senha@123", null, gerarCpf(), null, null));

        mockMvc.perform(get("/api/v1/clients")
                        .param("sort", "name,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name", is("Carlos")))
                .andExpect(jsonPath("$.content[1].name", is("Bruno")))
                .andExpect(jsonPath("$.content[2].name", is("Ana")));
    }

    @Test
    @DisplayName("Deve retornar uma página vazia quando não houver clientes")
    void getAllClientsPageable_WhenNoClients_ShouldReturnEmptyPage() throws Exception {
        mockMvc.perform(get("/api/v1/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements", is(0)));
    }

    @Test
    @DisplayName("Deve retornar um cliente e status 200 quando o ID existir")
    void getClientById_WhenIdExists_ShouldReturnClientAndStatus200() throws Exception {
        Client savedClient = clientRepository.save(new Client(null, "Bruno", LocalDate.now().minusYears(30), "bruno@email.com", "senha@123", null, gerarCpf(), null, null));
        String existingId = savedClient.getId();

        mockMvc.perform(get("/api/v1/clients/{id}", existingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(existingId)))
                .andExpect(jsonPath("$.name", is("Bruno")))
                .andExpect(jsonPath("$.email", is("bruno@email.com")))
                .andExpect(jsonPath("$.age", is(30)));
    }

    @Test
    @DisplayName("Deve retornar status 404 e corpo de erro padrão quando o ID não existir")
    void getClientById_WhenIdDoesNotExist_ShouldReturnStatus404AndErrorBody() throws Exception {
        String nonExistentId = UUID.randomUUID().toString();
        String expectedMessage = "O clinte informado não foi encontrado.";
        String expectedPath = "/api/v1/clients/" + nonExistentId;

        mockMvc.perform(get("/api/v1/clients/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("CLIENT_NOT_FOUND")))
                .andExpect(jsonPath("$.message", is(expectedMessage)))
                .andExpect(jsonPath("$.path", is(expectedPath)));
    }

    @Test
    @DisplayName("Deve atualizar um cliente com sucesso e retornar status 200")
    void updateClientById_WithValidDataAndExistingId_ShouldReturn200() throws Exception {
        String cpf = gerarCpf();
        Client existingClient = clientRepository.save(new Client(
                null,
                "Nome Antigo",
                LocalDate.of(1990, 1, 1),
                "antigo@email.com",
                "senha_antiga", // Senha antiga
                "89994574321",
                cpf,
                null,
                null
        ));
        String existingId = existingClient.getId();

        var updateDTO = new ClientUpdateDTO(
                "Nome Novo",
                LocalDate.of(1990, 1, 1),
                "novo@email.com",
                "nova_senha_123",
                "89994572322",
                cpf
        );

        mockMvc.perform(put("/api/v1/clients/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(existingId)))
                .andExpect(jsonPath("$.name", is("Nome Novo")))
                .andExpect(jsonPath("$.email", is("novo@email.com")))
                .andExpect(jsonPath("$.phone", is("89994572322")));

        Client updatedClientInDb = clientRepository.findById(existingId).get();
        assertThat(updatedClientInDb.getName()).isEqualTo("Nome Novo");
        assertThat(updatedClientInDb.getEmail()).isEqualTo("novo@email.com");
    }

    @Test
    @DisplayName("Deve retornar status 404 ao tentar atualizar um cliente com ID inexistente")
    void updateClientById_WithNonExistingId_ShouldReturn404() throws Exception {
        String nonExistingId = UUID.randomUUID().toString();
        var updateDTO = new ClientUpdateDTO(
                "Nome",
                LocalDate.of(1990, 1, 1),
                "novo@email.com",
                "senha@123",
                "89994572322",
                gerarCpf()
        );

        mockMvc.perform(put("/api/v1/clients/{id}", nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar status 422 ao tentar atualizar com dados inválidos")
    void updateClientById_WithInvalidData_ShouldReturn422() throws Exception {
        String cpf = gerarCpf();
        Client existingClient = clientRepository.save(new Client(
                null,
                "Nome Antigo",
                LocalDate.of(1990, 1, 1),
                "antigo@email.com",
                "senha_antiga", // Senha antiga
                "89994574321",
                cpf,
                null,
                null
        ));
        String existingId = existingClient.getId();

        var invalidUpdateDTO = new ClientUpdateDTO(
                "",
                LocalDate.of(1990, 1, 1),
                "emial",
                "senha@123",
                "89994572322",
                "1111111111111"
        );

        mockMvc.perform(put("/api/v1/clients/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdateDTO)))
                .andExpect(status().isUnprocessableEntity());

        Client clientInDb = clientRepository.findById(existingId).get();
        assertThat(clientInDb.getName()).isEqualTo("Nome Antigo");
    }

    @Test
    @DisplayName("Deve excluir um cliente com sucesso e retornar status 204")
    void deleteClientById_WhenIdExists_ShouldReturn204() throws Exception {
        Client clientToDelete = clientRepository.save(new Client(
                null,
                "Nome Antigo",
                LocalDate.of(1990, 1, 1),
                "antigo@email.com",
                "senha_antiga", // Senha antiga
                "89994574321",
                gerarCpf(),
                null,
                null
        ));
        String existingId = clientToDelete.getId();

        mockMvc.perform(delete("/api/v1/clients/{id}", existingId))
                .andExpect(status().isNoContent());

        assertThat(clientRepository.existsById(existingId)).isFalse();
    }

    @Test
    @DisplayName("Deve retornar status 404 ao tentar excluir um cliente com ID inexistente")
    void deleteClientById_WhenIdDoesNotExist_ShouldReturn404() throws Exception {
        String nonExistingId = UUID.randomUUID().toString();

        mockMvc.perform(delete("/api/v1/clients/{id}", nonExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar todos os clientes (paginado) quando nenhum filtro for aplicado")
    void searchByAttributes_WithNoFilters_ShouldReturnAllClients() throws Exception {
        Client ana = new Client(null, "Ana Silva", LocalDate.of(1990, 5, 15), "ana.silva@email.com", "senha@123", "89994352312", gerarCpf(), null, null);
        Client bruno = new Client(null, "Bruno Souza", LocalDate.of(1995, 10, 20), "bruno.souza@email.com", "senha@123", "89994352312", gerarCpf(), null, null);
        Client carlos = new Client(null, "Carlos Pereira", LocalDate.of(2000, 1, 30), "carlos.p@email.com", "senha@123", "89994352312", gerarCpf(), null, null);
        clientRepository.saveAll(List.of(ana, bruno, carlos));

        mockMvc.perform(get("/api/v1/clients/attributes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(3)));
    }

    @Test
    @DisplayName("Deve retornar clientes filtrando por parte do nome")
    void searchByAttributes_ByNameLike_ShouldReturnMatchingClients() throws Exception {
        Client ana = new Client(null, "Ana Silva", LocalDate.of(1990, 5, 15), "ana.silva@email.com", "senha@123", "89994352312", gerarCpf(), null, null);
        Client bruno = new Client(null, "Bruno Souza", LocalDate.of(1995, 10, 20), "bruno.souza@email.com", "senha@123", "89994352312", gerarCpf(), null, null);
        Client carlos = new Client(null, "Carlos Pereira", LocalDate.of(2000, 1, 30), "carlos.p@email.com", "senha@123", "89994352312", gerarCpf(), null, null);
        clientRepository.saveAll(List.of(ana, bruno, carlos));
        mockMvc.perform(get("/api/v1/clients/attributes")
                        .param("name", "Silva"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.content[0].name", is("Ana Silva")));
    }

    @Test
    @DisplayName("Deve retornar um cliente ao filtrar por CPF exato")
    void searchByAttributes_ByExactCpf_ShouldReturnOneClient() throws Exception {
        String cpf = gerarCpf();
        Client bruno = new Client(null, "Bruno Souza", LocalDate.of(1995, 10, 20), "bruno.souza@email.com", "senha@123", "89994352312", cpf, null, null);
        clientRepository.save(bruno);

        mockMvc.perform(get("/api/v1/clients/attributes")
                        .param("cpf", cpf))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.content[0].name", is("Bruno Souza")));
    }

    @Test
    @DisplayName("Deve retornar clientes dentro de um intervalo de datas de nascimento")
    void searchByAttributes_ByBirthdayRange_ShouldReturnMatchingClients() throws Exception {
        Client ana = new Client(null, "Ana Silva", LocalDate.of(1990, 5, 15), "ana.silva@email.com", "senha@123", "89994352312", gerarCpf(), null, null);
        Client bruno = new Client(null, "Bruno Souza", LocalDate.of(1995, 10, 20), "bruno.souza@email.com", "senha@123", "89994352312", gerarCpf(), null, null);
        Client carlos = new Client(null, "Carlos Pereira", LocalDate.of(2000, 1, 30), "carlos.p@email.com", "senha@123", "89994352312", gerarCpf(), null, null);
        clientRepository.saveAll(List.of(ana, bruno, carlos));

        mockMvc.perform(get("/api/v1/clients/attributes")
                        .param("birthdayStart", "1995-01-01")
                        .param("birthdayEnd", "2000-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.content[?(@.name == 'Bruno Souza')]").exists())
                .andExpect(jsonPath("$.content[?(@.name == 'Carlos Pereira')]").exists());
    }

    @Test
    @DisplayName("Deve retornar uma página vazia quando nenhum cliente corresponder ao filtro")
    void searchByAttributes_WithNonMatchingFilter_ShouldReturnEmptyPage() throws Exception {
        mockMvc.perform(get("/api/v1/clients/attributes")
                        .param("name", "Zebra"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(0)))
                .andExpect(jsonPath("$.content").isEmpty());
    }



    static String gerarCpf() {
        Random r = new Random();
        int[] d = new int[11];

        for (int i = 0; i < 9; i++) {
            d[i] = r.nextInt(10);
        }

        int s = 0;
        for (int i = 0, w = 10; i < 9; i++, w--) {
            s += d[i] * w;
        }
        d[9] = (s % 11 < 2) ? 0 : 11 - (s % 11);

        s = 0;
        for (int i = 0, w = 11; i < 10; i++, w--) {
            s += d[i] * w;
        }
        d[10] = (s % 11 < 2) ? 0 : 11 - (s % 11);

        return String.format("%d%d%d%d%d%d%d%d%d%d%d",
                d[0], d[1], d[2], d[3], d[4], d[5], d[6], d[7], d[8], d[9], d[10]);
    }
}
