package br.com.neoapp.api.security;

import br.com.neoapp.api.exceptions.ClientNotFound;
import br.com.neoapp.api.model.Client;
import br.com.neoapp.api.repository.ClientRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Serviço que fornece lógicas de autorização customizadas para serem utilizadas
 * com as anotações de segurança do Spring (e.g., {@code @PreAuthorize}).
 * <p>
 * Este bean é nomeado como "authorization" para ser facilmente referenciado
 * em expressões SpEL (Spring Expression Language), permitindo verificações de
 * permissão dinâmicas e baseadas no contexto da requisição.
 */
@Component("authorization")
public class AuthorizationService {

    private final ClientRepository clientRepository;

    /**
     * Constrói o serviço de autorização com as dependências necessárias.
     *
     * @param clientRepository O repositório para acessar os dados dos clientes.
     */
    public AuthorizationService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * Verifica se o usuário autenticado é o "dono" do recurso (cliente) que está tentando acessar.
     * <p>
     * Esta verificação é um pilar da autorização em nível de recurso, garantindo que um
     * usuário só possa visualizar ou modificar seus próprios dados. A lógica compara o ID do
     * usuário (extraído do claim 'sub' do token JWT) com o ID do recurso que está sendo solicitado.
     *
     * @param id             O ID do recurso (neste caso, o ID do cliente) a ser verificado.
     * @param authentication O objeto de autenticação do Spring Security, contendo os detalhes
     * do usuário logado (incluindo o JWT).
     * @return {@code true} se o ID do usuário no token for igual ao ID do recurso,
     * {@code false} caso contrário.
     * @throws ClientNotFound      se nenhum cliente for encontrado com o {@code id} fornecido.
     * @throws ClassCastException se o principal da autenticação não for um {@link Jwt},
     * o que geralmente indica um problema de configuração de segurança ou de teste.
     */
    public boolean isAuthorized(String id, Authentication authentication){
        Client client = clientRepository.findById(id).orElseThrow(
                () -> new ClientNotFound("O clinte informado não foi encontrado."));

        Jwt jwt = (Jwt) authentication.getPrincipal();

        return  jwt.getSubject().equals(id);
    }
}
