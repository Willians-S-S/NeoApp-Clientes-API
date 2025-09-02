package br.com.neoapp.api.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * Classe de configuração central para beans da aplicação.
 * <p>
 * Esta classe é responsável por definir e expor beans essenciais que são utilizados
 * em toda a aplicação, especialmente aqueles relacionados à segurança, como a codificação
 * de senhas e a manipulação de tokens JWT.
 */
@Configuration
public class ApplicationConfig {

    /**
     * Chave pública RSA injetada a partir da propriedade 'jwt.public.key'.
     * Utilizada para a decodificação e verificação da assinatura de tokens JWT.
     */
    @Value("${jwt.public.key}")
    private RSAPublicKey key;

    /**
     * Chave privada RSA injetada a partir da propriedade 'jwt.private.key'.
     * Utilizada para a codificação e assinatura de novos tokens JWT.
     */
    @Value("${jwt.private.key}")
    private RSAPrivateKey priv;

    /**
     * Define o bean {@link PasswordEncoder} para a aplicação.
     * <p>
     * Utiliza o {@link BCryptPasswordEncoder}, que é o algoritmo recomendado pelo Spring Security
     * para o hashing seguro de senhas. Este bean será injetado em serviços que precisam
     * verificar ou codificar senhas de usuários.
     *
     * @return uma instância de {@code BCryptPasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Define o bean {@link JwtDecoder} para a aplicação.
     * <p>
     * Este decodificador é configurado com a chave pública RSA e será utilizado pelo
     * Spring Security Resource Server para validar a assinatura e os claims dos
     * tokens JWT recebidos em requisições protegidas.
     *
     * @return uma instância de {@code NimbusJwtDecoder} configurada com a chave pública.
     */
    @Bean
    JwtDecoder decoder(){
        return NimbusJwtDecoder.withPublicKey(key).build();
    }

    /**
     * Define o bean {@link JwtEncoder} para a aplicação.
     * <p>
     * Este codificador é configurado com o par de chaves RSA (pública e privada).
     * Será utilizado por serviços como o {@code JwtService} para criar e assinar
     * novos tokens JWT para usuários que se autenticam com sucesso.
     *
     * @return uma instância de {@code NimbusJwtEncoder} configurada com o par de chaves.
     */
    @Bean
    JwtEncoder jwtEncoder(){
        var jwk = new RSAKey.Builder(key).privateKey(priv).build();
        var jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }
}
