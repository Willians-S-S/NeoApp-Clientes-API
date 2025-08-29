package br.com.neoapp.api.service;

import br.com.neoapp.api.controller.dto.ClientRequestDTO;
import br.com.neoapp.api.controller.dto.ClientResponseDTO;
import br.com.neoapp.api.exceptions.CpfExistsException;
import br.com.neoapp.api.exceptions.EmailExistsException;
import br.com.neoapp.api.mapper.ClientMapper;
import br.com.neoapp.api.model.Client;
import br.com.neoapp.api.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientMapper clientMapper;

    public ClientResponseDTO creatClient(ClientRequestDTO clientRequestDTO) {
        if (clientRepository.existsByEmail(clientRequestDTO.email())){
            throw new EmailExistsException("O endereço de e-mail informado já está registrado.");
        }

        if (clientRepository.existsByCpf(clientRequestDTO.cpf())){
            throw new CpfExistsException("O CPF informado já está registrado.");
        }

        Client client = clientMapper.toEntity(clientRequestDTO);
        client = clientRepository.save(client);
        return clientMapper.toResponse(client);
    }


}
