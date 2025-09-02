package br.com.neoapp.api.mapper;

import br.com.neoapp.api.controller.dto.ClientRequestDTO;
import br.com.neoapp.api.controller.dto.ClientResponseDTO;
import br.com.neoapp.api.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;

/**
 * Interface de mapeamento gerenciada pelo MapStruct para conversões entre a entidade
 * {@link Client} e seus respectivos Data Transfer Objects (DTOs).
 * <p>
 * Configurada como um componente Spring, esta interface é responsável por desacoplar
 * a camada de domínio da camada de apresentação (API), automatizando a transformação
 * de objetos.
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {LocalDate.class, OffsetDateTime.class, Period.class}
)
public interface ClientMapper {
    /**
     * Converte um {@link ClientRequestDTO} em uma entidade {@link Client}.
     * O campo 'id' é explicitamente ignorado, pois é gerado pelo banco de dados
     * no momento da criação.
     *
     * @param clientRequestDTO O DTO com os dados de entrada para criar um cliente.
     * @return A entidade {@code Client} pronta para ser persistida.
     */
    @Mapping(target = "id", ignore = true)
    Client toEntity(ClientRequestDTO clientRequestDTO);

    /**
     * Converte uma entidade {@link Client} em um {@link ClientResponseDTO}.
     * O campo 'age' (idade) é calculado dinamicamente com base na data de nascimento
     * do cliente, utilizando o método de apoio {@link #calculateAge(LocalDate)}.
     *
     * @param client A entidade {@code Client} a ser convertida.
     * @return O DTO {@code ClientResponseDTO} formatado para ser exposto na API.
     */
    @Mapping(target = "age", expression = "java(calculateAge(client.getBirthday()))")
    ClientResponseDTO toResponse(Client client);

    /**
     * Método de apoio (default) que calcula a idade de uma pessoa com base na sua data de nascimento.
     *
     * @param birthDate A data de nascimento.
     * @return A idade calculada em anos.
     */
    default Integer calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Método de apoio (default) que retorna a data e hora atuais com fuso horário.
     *
     * @return O {@code OffsetDateTime} representando o momento atual.
     */
    default OffsetDateTime now(){
        return  OffsetDateTime.now();
    }

    /**
     * Converte uma {@link Page} de entidades {@link Client} em uma {@link Page}
     * de DTOs {@link ClientResponseDTO}, aplicando o mapeamento a cada item da página.
     *
     * @param clientsPage A página de entidades de cliente vinda do repositório.
     * @return A página correspondente de DTOs de resposta.
     */
    default Page<ClientResponseDTO> toPageResponse(Page<Client> clientsPage) {
        return clientsPage.map(this::toResponse);
    }
}
