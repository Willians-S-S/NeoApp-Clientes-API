package br.com.neoapp.api.security;

import br.com.neoapp.api.exceptions.ClientNotFound;
import br.com.neoapp.api.repository.ClientRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private ClientRepository clientRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return clientRepository.findByEmail(username)
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> new ClientNotFound("O clinte informado não foi encontrado."));
    }
}
