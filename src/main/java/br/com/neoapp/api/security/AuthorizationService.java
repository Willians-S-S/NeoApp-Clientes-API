package br.com.neoapp.api.security;

import br.com.neoapp.api.exceptions.ClientNotFound;
import br.com.neoapp.api.model.Client;
import br.com.neoapp.api.repository.ClientRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("authorization")
public class AuthorizationService {

    private final ClientRepository clientRepository;

    public AuthorizationService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public boolean isAuthorized(String id, Authentication authentication){
        Client client = clientRepository.findById(id).orElseThrow(
                () -> new ClientNotFound("O clinte informado não foi encontrado."));

        Jwt jwt = (Jwt) authentication.getPrincipal();

        System.out.println(jwt.getClaims().get("sub"));
        System.out.println(id);
        return  jwt.getClaims().get("sub").equals(id);
    }
}
