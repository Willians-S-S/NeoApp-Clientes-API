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

/**
 * Serviço responsável pela geração de JSON Web Tokens (JWT).
 * <p>
 * Utiliza o {@link JwtEncoder} do Spring Security para criar e assinar tokens
 * que serão utilizados para autenticar e autorizar requisições na API.
 */
@Service
public class JwtService {
    private final JwtEncoder encoder;

    /**
     * Constrói o serviço com o codificador de JWT.
     *
     * @param encoder O {@link JwtEncoder} injetado pelo Spring, responsável pela
     * codificação e assinatura dos tokens.
     */
    public JwtService(JwtEncoder encoder){
        this.encoder = encoder;
    }

    /**
     * Gera um token JWT para um cliente autenticado.
     * <p>
     * O token gerado contém claims (informações) padrão como emissor (issuer),
     * data de expiração (expiresAt), e o ID do cliente como 'subject' (sub).
     * Além disso, inclui um claim customizado 'scope' que contém os papéis (roles)
     * do usuário em uma string separada por espaços.
     *
     * @param client A entidade do cliente para a qual o token será gerado.
     * @return um {@link LoginResponse} contendo o token de acesso (JWT) e seu
     * tempo de expiração em segundos.
     * @throws RuntimeException se ocorrer qualquer erro durante a geração do token.
     */
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
