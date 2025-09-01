package br.com.neoapp.api.security;

import br.com.neoapp.api.controller.dto.LoginRequest;
import br.com.neoapp.api.controller.dto.LoginResponse;
import br.com.neoapp.api.exceptions.EmailOrPassworInvalid;
import br.com.neoapp.api.model.Client;
import br.com.neoapp.api.repository.ClientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
public class AuthenticationService {

    private final ClientRepository clientRepository;

    PasswordEncoder passwordEncoder;

    JwtService jwtService;

    public AuthenticationService(ClientRepository clientRepository, PasswordEncoder passwordEncoder, JwtService jwtService){
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse authenticate(LoginRequest loginRequest){
        Client client = clientRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new EmailOrPassworInvalid("Email ou senha inválido."));

        String passEncoder = passwordEncoder.encode(loginRequest.password());

        if (!passwordEncoder.matches(passEncoder, client.getPassword())){
            throw new EmailOrPassworInvalid("Email ou senha inválido.");
        }

        return jwtService.genereteToken(client);
    }
}
