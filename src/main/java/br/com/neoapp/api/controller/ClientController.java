package br.com.neoapp.api.controller;

import br.com.neoapp.api.controller.dto.ClientRequestDTO;
import br.com.neoapp.api.controller.dto.ClientResponseDTO;
import br.com.neoapp.api.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientResponseDTO> creatClient(@RequestBody ClientRequestDTO clientRequestDTO){

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
