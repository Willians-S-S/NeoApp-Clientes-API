package br.com.neoapp.api.controller;

import br.com.neoapp.api.controller.dto.ClientRequestDTO;
import br.com.neoapp.api.controller.dto.LoginRequest;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesRegex;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DisplayName("Testes de Integração para AuthController")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ClientRequestDTO validRequestDTO;
    private ClientRequestDTO invalidRequestDTO;
    private ClientRequestDTO emailInvalidRequestDTO;
    private ClientRequestDTO nameInvalidRequestDTO;

    @BeforeEach
    void setUp() {
        clientRepository.deleteAll();
        Client client = new Client();
        client.setName("Usuário Teste");
        client.setEmail("user@test.com");
        client.setPassword(passwordEncoder.encode("senha@123"));
        client.setCpf("97765369059");
        client.setBirthday(LocalDate.of(2000, 1, 1));
        clientRepository.save(client);

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
    @DisplayName("Deve autenticar com credenciais válidas e retornar status 200")
    void login_WithValidCredentials_ShouldReturnTokenAndStatus200() throws Exception {
        LoginRequest validLoginRequest = new LoginRequest("user@test.com", "senha@123");
        String jwtRegex = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$";

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.accessToken", matchesRegex(jwtRegex)));
    }


    @Test
    @DisplayName("Deve retornar status 401 para usuário inexistente")
    void login_WithNonExistentUser_ShouldReturnStatus401() throws Exception {
        LoginRequest nonExistentUserRequest = new LoginRequest("naoexiste@test.com", "qualquer-senha");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistentUserRequest)))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @DisplayName("Deve retornar status 401 para senha inválida")
    void login_WithInvalidPassword_ShouldReturnStatus401() throws Exception {
        LoginRequest invalidPasswordRequest = new LoginRequest("user@test.com", "senha-errada");

        mockMvc.perform(post("/api/v1/auth/login") // Use POST
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPasswordRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve criar cliente e retornar status 201 ao receber dados válidos")
    void creatClientWithValidDataShouldPersistClientAndReturn201() throws Exception {
        clientRepository.deleteAll();
        mockMvc.perform(post("/api/v1/auth/sign")
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

        mockMvc.perform(post("/api/v1/auth/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("EMAIL_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("O endereço de e-mail informado já está registrado."))
                .andExpect(jsonPath("$.path").value("/api/v1/auth/sign"));
    }

    @Test
    @DisplayName("Deve retornar status 409 ao tentar criar cliente com CPF já existente")
    void creatClientWithExistingCpfShouldReturn409() throws Exception {
        clientRepository.save(objectMapper.convertValue(validRequestDTO, br.com.neoapp.api.model.Client.class));

        mockMvc.perform(post("/api/v1/auth/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("CPF_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("O CPF informado já está registrado."))
                .andExpect(jsonPath("$.path").value("/api/v1/auth/sign"));
    }

    @Test
    @DisplayName("Deve retornar status 422 ao receber múltiplos campos inválidos")
    void creatClientWithInvalidDataShouldReturn422() throws Exception {
        invalidRequestDTO = new ClientRequestDTO("", null, "email-invalido", "123", null, "cpf-invalido");

        mockMvc.perform(post("/api/v1/auth/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Dados inválidos. Verifique os erros de cada campo."))
                .andExpect(jsonPath("$.path").value("/api/v1/auth/sign"));
    }

    @Test
    @DisplayName("Deve retornar status 422 para formato de e-mail inválido")
    void creatClientWithInvalidEmailShouldReturn422() throws Exception {

        mockMvc.perform(post("/api/v1/auth/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailInvalidRequestDTO)))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Dados inválidos. Verifique os erros de cada campo."))
                .andExpect(jsonPath("$.path").value("/api/v1/auth/sign"))
                .andExpect(jsonPath("$.errors[0].fieldName").value("email"))
                .andExpect(jsonPath("$.errors[0].message").value("Formato de e-mail inválido."));
    }

    @Test
    @DisplayName("Deve retornar status 422 para nome fora do tamanho permitido")
    void creatClientWithInvalidNameShouldReturn422() throws Exception {

        mockMvc.perform(post("/api/v1/auth/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nameInvalidRequestDTO)))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Dados inválidos. Verifique os erros de cada campo."))
                .andExpect(jsonPath("$.path").value("/api/v1/auth/sign"))
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
        mockMvc.perform(post("/api/v1/auth/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nameEmptyInvalidRequestDTO)))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Dados inválidos. Verifique os erros de cada campo."))
                .andExpect(jsonPath("$.path").value("/api/v1/auth/sign"))
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
        mockMvc.perform(post("/api/v1/auth/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nameEmptyInvalidRequestDTO)))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Dados inválidos. Verifique os erros de cada campo."))
                .andExpect(jsonPath("$.path").value("/api/v1/auth/sign"))
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
        mockMvc.perform(post("/api/v1/auth/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nameEmptyInvalidRequestDTO)))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Dados inválidos. Verifique os erros de cada campo."))
                .andExpect(jsonPath("$.path").value("/api/v1/auth/sign"))
                .andExpect(jsonPath("$.errors[0].fieldName").value("password"))
                .andExpect(jsonPath("$.errors[0].message").value("A senha deve ter no mínimo 8 caracteres."));
    }
}