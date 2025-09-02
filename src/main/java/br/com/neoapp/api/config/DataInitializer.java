package br.com.neoapp.api.config;

import br.com.neoapp.api.enums.RoleName;
import br.com.neoapp.api.model.Client;
import br.com.neoapp.api.model.Role;
import br.com.neoapp.api.repository.ClientRepository;
import br.com.neoapp.api.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void initData(){

        Role roleUser = createOrGetRole(RoleName.USER);
        Role roleAdmin = createOrGetRole(RoleName.ADMIN);

        if (clientRepository.findByEmail("admin@email.com").isEmpty()){
            List<Role> listRole = new ArrayList<>();
            listRole.add(roleAdmin);

            Client clientAdmin = new Client(
                    null,
                    "Willians Silva",
                    LocalDate.of(2003, 1, 28),
                    "admin@email.com",
                    passwordEncoder.encode("senhaforte"),
                    "89994776644",
                    "67283621008",
                    null,
                    null,
                    listRole
            );

            clientRepository.save(clientAdmin);
            System.out.println("Dados iniciais criados com sucesso!");
        }
    }

    private Role createOrGetRole(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(roleName);
                    return roleRepository.save(newRole);
                });
    }
}