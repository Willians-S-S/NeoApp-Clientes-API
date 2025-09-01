package br.com.neoapp.api.controller;

import br.com.neoapp.api.controller.dto.ClientRequestDTO;
import br.com.neoapp.api.controller.dto.ClientResponseDTO;
import br.com.neoapp.api.controller.dto.LoginRequest;
import br.com.neoapp.api.controller.dto.LoginResponse;
import br.com.neoapp.api.exceptions.StandardError;
import br.com.neoapp.api.exceptions.ValidationError;
import br.com.neoapp.api.security.AuthenticationService;
import br.com.neoapp.api.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ClientService clientService;

    @GetMapping(value = "/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok()
                .body(authenticationService.authenticate(loginRequest));
    }

    @Operation(
            summary = "Criar um novo cliente",
            description = "Cria um novo cliente no sistema com base nos dados fornecidos. " +
                    "Após a criação, retorna o status 201 e a localização do novo recurso."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Cliente criado com sucesso",
                    content = { @Content(schema = @Schema(implementation = ClientResponseDTO.class), mediaType = "application/json") },
                    headers = { @Header(name = "Location", description = "URI para acessar o cliente recém-criado") }
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflito: O e-mail ou CPF informado já está cadastrado no sistema.",
                    content = {@Content(schema = @Schema(implementation = StandardError.class)) }
            ),
            @ApiResponse(responseCode = "422", description = "Dados inválidos fornecidos", content = { @Content(schema = @Schema(implementation = ValidationError.class)) }),
    })
    @PostMapping(value = "/sign")
    public ResponseEntity<ClientResponseDTO> creatClient(@RequestBody @Valid ClientRequestDTO clientRequestDTO){

        ClientResponseDTO clientResponseDTO = clientService.creatClient(clientRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(clientResponseDTO.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(clientResponseDTO);
    }
}
