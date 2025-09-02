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

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Classe de configuração central para beans da aplicação.
 * <p>
 * Esta classe é responsável por definir e expor beans essenciais que são utilizados
 * em toda a aplicação, especialmente aqueles relacionados à segurança, como a codificação
 * de senhas e a manipulação de tokens JWT. As chaves RSA são carregadas de variáveis
 * de ambiente.
 */
@Configuration
public class ApplicationConfig {

    // 1. Injeta as chaves como Strings Base64 a partir das variáveis de ambiente
    @Value("${jwt.public.key.base64}")
    private String publicKeyBase64;

    @Value("${jwt.private.key.base64}")
    private String privateKeyBase64;

    /**
     * Define o bean {@link PasswordEncoder} para a aplicação.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Cria e expõe o bean da chave pública RSA.
     * <p>
     * A chave é lida da variável de ambiente, decodificada de Base64 e convertida
     * em um objeto {@link RSAPublicKey}.
     *
     * @return A instância da chave pública RSA.
     * @throws Exception se ocorrer um erro ao processar a chave.
     */
    @Bean
    public RSAPublicKey rsaPublicKey() throws Exception {
        String publicKeyPEM = new String(Base64.getDecoder().decode(publicKeyBase64));
        String publicKeyContent = publicKeyPEM
                .replaceAll("\\n", "")
                .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll("-----END PUBLIC KEY-----", "");

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    /**
     * Cria e expõe o bean da chave privada RSA.
     * <p>
     * A chave é lida da variável de ambiente, decodificada de Base64 e convertida
     * em um objeto {@link RSAPrivateKey}.
     *
     * @return A instância da chave privada RSA.
     * @throws Exception se ocorrer um erro ao processar a chave.
     */
    @Bean
    public RSAPrivateKey rsaPrivateKey() throws Exception {
        String privateKeyPEM = new String(Base64.getDecoder().decode(privateKeyBase64));
        String privateKeyContent = privateKeyPEM
                .replaceAll("\\n", "")
                .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----", "");

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }


    /**
     * Define o bean {@link JwtDecoder} para a aplicação.
     * <p>
     * Este método agora recebe o bean da chave pública como parâmetro,
     * garantindo que ele seja construído primeiro.
     *
     * @param publicKey A chave pública RSA para configurar o decodificador.
     * @return uma instância de {@code NimbusJwtDecoder}.
     */
    @Bean
    JwtDecoder decoder(RSAPublicKey publicKey){
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    /**
     * Define o bean {@link JwtEncoder} para a aplicação.
     * <p>
     * Este método agora recebe os beans das chaves pública e privada como parâmetros.
     *
     * @param publicKey A chave pública RSA.
     * @param privateKey A chave privada RSA.
     * @return uma instância de {@code NimbusJwtEncoder}.
     */
    @Bean
    JwtEncoder jwtEncoder(RSAPublicKey publicKey, RSAPrivateKey privateKey){
        var jwk = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
        var jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }
}
