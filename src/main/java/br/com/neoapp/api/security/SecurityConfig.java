package br.com.neoapp.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Classe de configuração central para as políticas de segurança da aplicação.
 * <p>
 * Anotada com {@code @EnableWebSecurity}, esta classe habilita o suporte de segurança web do Spring.
 * A anotação {@code @EnableMethodSecurity} ativa a segurança em nível de método, permitindo o uso
 * de anotações como {@code @PreAuthorize} nos controllers e serviços para controle de acesso fino.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Define a cadeia de filtros de segurança (SecurityFilterChain) que aplica as regras de
     * autorização e autenticação para as requisições HTTP.
     * <p>
     * A configuração estabelece as seguintes políticas:
     * <ul>
     * <li><b>CSRF (Cross-Site Request Forgery):</b> Desabilitado, uma prática comum para APIs REST stateless.</li>
     * <li><b>Autorização de Requisições:</b>
     * <ul>
     * <li>Os endpoints de login ({@code /api/v1/auth/login}) e registro ({@code /api/v1/auth/sign}) são públicos e não exigem autenticação.</li>
     * <li>Todas as outras requisições exigem que o usuário esteja autenticado.</li>
     * </ul>
     * </li>
     * <li><b>Autenticação:</b> O suporte para HTTP Basic está habilitado como uma forma de autenticação.</li>
     * <li><b>Servidor de Recurso OAuth2:</b> A aplicação é configurada para atuar como um Resource Server,
     * validando tokens JWT como o método principal de autenticação para as requisições protegidas.</li>
     * </ul>
     *
     * @param http o construtor {@link HttpSecurity} para configurar a cadeia de filtros.
     * @return a {@link SecurityFilterChain} construída e configurada.
     * @throws Exception se ocorrer um erro durante a configuração da segurança.
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers("/api/v1/auth/login", "/api/v1/auth/sign").permitAll()
                                .anyRequest().authenticated()
                ).httpBasic(Customizer.withDefaults())
                .oauth2ResourceServer(
                        conf -> conf.jwt(Customizer.withDefaults())
                );
        return http.build();
    }
}

