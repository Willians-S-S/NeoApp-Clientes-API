package br.com.neoapp.api.security;

import br.com.neoapp.api.controller.dto.LoginRequest;
import br.com.neoapp.api.controller.dto.LoginResponse;
import br.com.neoapp.api.exceptions.EmailOrPassworInvalid;
import br.com.neoapp.api.model.Client;
import br.com.neoapp.api.repository.ClientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


/**
 * Serviço responsável por centralizar a lógica de autenticação de usuários.
 * <p>
 * Esta classe lida com a validação de credenciais (e-mail e senha) e, em caso de
 * sucesso, coordena a geração de um token JWT para o usuário autenticado.
 */
@Service
public class AuthenticationService {

    private final ClientRepository clientRepository;

    PasswordEncoder passwordEncoder;

    JwtService jwtService;

    /**
     * Constrói o serviço de autenticação com as dependências necessárias injetadas.
     *
     * @param clientRepository Repositório para acesso aos dados dos clientes.
     * @param passwordEncoder  Codificador utilizado para validar as senhas.
     * @param jwtService       Serviço responsável pela geração de tokens JWT.
     */
    public AuthenticationService(ClientRepository clientRepository, PasswordEncoder passwordEncoder, JwtService jwtService){
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Autentica um usuário com base nas credenciais fornecidas em {@link LoginRequest}.
     * <p>
     * O processo consiste em:
     * <ol>
     * <li>Buscar o cliente pelo e-mail fornecido.</li>
     * <li>Comparar a senha da requisição com a senha armazenada (hash) do cliente.</li>
     * <li>Gerar um token JWT em caso de sucesso na validação.</li>
     * </ol>
     *
     * @param loginRequest O DTO contendo o e-mail и a senha para a tentativa de login.
     * @return um {@link LoginResponse} contendo o token de acesso e seu tempo de expiração.
     * @throws EmailOrPassworInvalid se o e-mail não for encontrado na base de dados ou se a senha não corresponder.
     */
    public LoginResponse authenticate(LoginRequest loginRequest){
        Client client = clientRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new EmailOrPassworInvalid("Email ou senha inválido."));

        if (!passwordEncoder.matches(loginRequest.password(), client.getPassword())){
            throw new EmailOrPassworInvalid("Email ou senha inválido.");
        }

        return jwtService.genereteToken(client);
    }
}
