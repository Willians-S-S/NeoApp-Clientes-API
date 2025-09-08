package br.com.neoapp.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "NeoApp Clientes API",
                version = "1.0.0",
                description = "API RESTful responsável pelo cadastro e gerenciamento de clientes (pessoa física) para a plataforma NeoApp. " +
                        "Oferece funcionalidades completas de CRUD (Create, Read, Update, Delete), listagem paginada e busca por múltiplos atributos cadastrais. " +
                        "Cada cliente retornado inclui a idade calculada dinamicamente a partir da data de nascimento. " +
                        "O acesso aos endpoints é protegido por autenticação via token JWT.",
                contact = @Contact(
                        name = "Willians Silva Santos",
                        email = "willianssilva@ufpi.edu.br"
                )
        )
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}
