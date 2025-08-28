package br.com.neoapp.api.service;

import br.com.neoapp.api.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;
}
