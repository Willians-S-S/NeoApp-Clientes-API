package br.com.neoapp.api.security;


import br.com.neoapp.api.controller.dto.LoginResponse;
import br.com.neoapp.api.enums.RoleName;
import br.com.neoapp.api.model.Client;
import br.com.neoapp.api.model.Role;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private final JwtEncoder encoder;

    public JwtService(JwtEncoder encoder){
        this.encoder = encoder;
    }

    public LoginResponse genereteToken(Client client){
        try {
            Instant now = Instant.now();
            long expire = 300L;

            var scopes = client.getRoles()
                    .stream()
                    .map(Role::getName)
                    .map(RoleName::name)
                    .collect(Collectors.joining(" "));

            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("Api")
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(expire))
                    .subject(client.getId())
                    .claim("scope", scopes)
                    .build();

            String token = encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

            return new LoginResponse(token, expire);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível autenticar o usuário.");
        }
    }

}
