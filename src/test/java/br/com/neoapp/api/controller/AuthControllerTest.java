package br.com.neoapp.api.controller;

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
}