package br.com.neoapp.api.controller;

import br.com.neoapp.api.controller.dto.ClientRequestDTO;
import br.com.neoapp.api.repository.ClientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ClientControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientRepository clientRepository;

    private ClientRequestDTO validRequestDTO;
    private ClientRequestDTO invalidRequestDTO;

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
    }

    @Test
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
}
