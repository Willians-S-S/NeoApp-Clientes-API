package br.com.neoapp.api.controller;

import br.com.neoapp.api.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(name = "/api/v1/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;
}
