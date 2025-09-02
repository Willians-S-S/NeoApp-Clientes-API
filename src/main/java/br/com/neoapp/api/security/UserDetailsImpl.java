package br.com.neoapp.api.security;

import br.com.neoapp.api.model.Client;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Implementação da interface {@link UserDetails} do Spring Security.
 * <p>
 * Esta classe atua como um "adaptador" entre a entidade de domínio {@link Client}
 * e o framework do Spring Security. Ela fornece as informações essenciais do usuário
 * (username, password, authorities) de uma forma que o Spring Security pode entender
 * e utilizar para os processos de autenticação e autorização.
 */
public class UserDetailsImpl implements UserDetails {

    private final Client client;

    /**
     * Constrói uma instância de UserDetails com base em uma entidade Client.
     *
     * @param client A entidade {@link Client} contendo os dados do usuário.
     */
    public UserDetailsImpl(Client client){
        this.client = client;
    }

    /**
     * Retorna as permissões (papéis/roles) concedidas ao usuário.
     * <p>
     * Converte a lista de {@link br.com.neoapp.api.model.Role} do cliente em uma coleção
     * de {@link GrantedAuthority}, que é o formato esperado pelo Spring Security.
     *
     * @return uma coleção de permissões do usuário.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return client.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
    }

    /**
     * Retorna a senha (hash) usada para autenticar o usuário.
     *
     * @return a senha criptografada do cliente.
     */
    @Override
    public String getPassword() {
        return client.getPassword();
    }

    /**
     * Retorna o nome de usuário usado para autenticar o usuário.
     * Neste sistema, o e-mail do cliente é utilizado como nome de usuário.
     *
     * @return o e-mail do cliente.
     */
    @Override
    public String getUsername() {
        return client.getEmail();
    }
}
