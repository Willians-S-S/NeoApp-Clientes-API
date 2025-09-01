package br.com.neoapp.api.controller;

import br.com.neoapp.api.controller.dto.ClientRequestDTO;
import br.com.neoapp.api.controller.dto.ClientResponseDTO;
import br.com.neoapp.api.controller.dto.ClientUpdateDTO;
import br.com.neoapp.api.exceptions.ClientNotFound;
import br.com.neoapp.api.exceptions.StandardError;
import br.com.neoapp.api.exceptions.ValidationError;
import br.com.neoapp.api.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;

@Tag(name = "Clientes", description = "Endpoints para o gerenciamento de clientes")
@RestController
@RequestMapping(value = "/api/v1/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

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
    @PostMapping
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

    @Operation(
            summary = "Listar todos os clientes com paginação",
            description = "Retorna uma lista paginada de todos os clientes cadastrados no sistema. " +
                    "Os parâmetros de paginação como `page`, `size` e `sort` podem ser enviados na URL."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Busca os clientes com páginação.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            ),
    })
    @GetMapping
    public ResponseEntity<Page<ClientResponseDTO>> getAllClientsPageable(Pageable pageable){
        return ResponseEntity.ok().body(clientService.getAllClientsPageable(pageable));
    }

    @Operation(
            summary = "Buscar cliente por ID",
            description = "Retorna os detalhes de um cliente específico com base no seu ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cliente encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClientResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cliente não encontrado para o ID fornecido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClientNotFound.class)
                    )
            ),
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<ClientResponseDTO> getClientById(@PathVariable String id){
        return ResponseEntity.ok().body(clientService.getClientById(id));
    }

    @Operation(
            summary = "Atualizar um cliente por ID",
            description = "Atualiza os dados de um cliente existente com base no seu ID. Esta operação " +
                    "substitui os dados do cliente com as informações fornecidas no corpo da requisição."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cliente atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClientResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Dados inválidos fornecidos",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationError.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "O clinte informado não foi encontrado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StandardError.class)
                    )
            )
    })
    @PutMapping(value = "/{id}")
    public ResponseEntity<ClientResponseDTO> updateClientById(@PathVariable String id,
                                                              @Valid @RequestBody ClientUpdateDTO clientUpdateDTO){
        return ResponseEntity.ok().body(clientService.updateClientById(id, clientUpdateDTO));
    }

    @Operation(
            summary = "Excluir um cliente por ID",
            description = "Remove permanentemente um cliente do sistema com base no seu ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Cliente excluído com sucesso. Nenhum conteúdo no corpo da resposta.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cliente não encontrado para o ID fornecido",
                    content = @Content
            )
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteClientById(@PathVariable String id){
        clientService.deleteClientById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Buscar clientes por múltiplos atributos (filtro dinâmico)",
            description = "Retorna uma lista paginada de clientes com base em uma combinação de filtros opcionais. " +
                    "Todos os parâmetros são opcionais. Se nenhum filtro for fornecido, retornará todos os clientes de forma paginada. " +
                    "O formato para datas é AAAA-MM-DD."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Busca realizada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
            )
    })
    @GetMapping(value = "/attributes")
    public ResponseEntity<Page<ClientResponseDTO>> getAllClientsWithAttributesPage(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthdayStart,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthdayEnd,
            Pageable pageable){
        return ResponseEntity.ok().body(clientService.
                getAllClientsWithAttributesPage(
                        name,
                        email,
                        cpf,
                        phone,
                        birthdayStart,
                        birthdayEnd,
                        pageable));
    }

    @GetMapping(value = "/one-client-attributes")
    public ResponseEntity<ClientResponseDTO> getClientsWithAttributes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthday){

        return ResponseEntity.ok().body(clientService.
                getClientsWithAttributes(
                        name,
                        email,
                        cpf,
                        phone,
                        birthday
                        ));
    }
}
