package br.com.neoapp.api.service;

import br.com.neoapp.api.controller.dto.ClientRequestDTO;
import br.com.neoapp.api.controller.dto.ClientResponseDTO;
import br.com.neoapp.api.model.Client;
import br.com.neoapp.api.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;



    public Client requestDTOToClient(ClientRequestDTO clientRequestDTO){
        Client client = new Client();
        client.setName(clientRequestDTO.name());
        client.setEmail(clientRequestDTO.email());
        client.setCpf(clientRequestDTO.cpf());
        client.setPhone(clientRequestDTO.phone());
        client.setPassword(clientRequestDTO.password());
        client.setBirthday(clientRequestDTO.birthday());
        client.setCreatAt(OffsetDateTime.now());
        client.setUpdateAt(OffsetDateTime.now());
    }

    public ClientResponseDTO clientToResponseDTO(Client client){

        Integer age = Period.between(client.getBirthday(), LocalDate.now()).getYears();

        return new ClientResponseDTO(client.getId(),
                client.getName(),
                age,
                client.getEmail(),
                client.getPhone(),
                client.getCpf(),
                client.getCreatAt(),
                client.getUpdateAt()
                );

    }
}
