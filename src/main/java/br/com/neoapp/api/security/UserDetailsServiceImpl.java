package br.com.neoapp.api.security;

import br.com.neoapp.api.exceptions.ClientNotFound;
import br.com.neoapp.api.repository.ClientRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementação da interface {@link UserDetailsService} do Spring Security.
 * <p>
 * Este serviço é o ponto de integração entre o mecanismo de autenticação do Spring
 * e o repositório de dados do usuário. Sua única responsabilidade é carregar os
 * detalhes de um usuário (neste caso, um {@link br.com.neoapp.api.model.Client})
 * com base em seu nome de usuário (que, para esta aplicação, é o e-mail).
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private ClientRepository clientRepository;

    /**
     * Localiza um usuário com base em seu nome de usuário (e-mail).
     * <p>
     * Este método é chamado pelo AuthenticationManager do Spring Security durante o
     * processo de autenticação para obter os detalhes do usuário que está tentando
     * fazer login.
     *
     * @param username o nome de usuário (neste caso, o e-mail) que identifica o usuário
     * cujos dados são solicitados.
     * @return um objeto {@link UserDetails} (na forma de {@link UserDetailsImpl})
     * contendo as informações do usuário encontrado.
     * @throws UsernameNotFoundException se o usuário não puder ser encontrado com o e-mail
     * fornecido (aqui representado pela exceção customizada {@link ClientNotFound}).
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return clientRepository.findByEmail(username)
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> new ClientNotFound("O clinte informado não foi encontrado."));
    }
}
