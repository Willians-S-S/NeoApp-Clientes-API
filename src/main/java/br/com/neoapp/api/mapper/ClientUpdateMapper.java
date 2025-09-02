package br.com.neoapp.api.mapper;

import br.com.neoapp.api.controller.dto.ClientUpdateDTO;
import br.com.neoapp.api.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Interface de mapeamento do MapStruct especializada para atualizar uma entidade {@link Client}
 * a partir de um {@link ClientUpdateDTO}.
 * <p>
 * A estratégia {@code NullValuePropertyMappingStrategy.IGNORE} é utilizada para garantir
 * que apenas os campos não nulos do DTO sejam atualizados na entidade, o que é ideal
 * para operações de atualização parcial (PATCH).
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClientUpdateMapper {
    void updateToClient(ClientUpdateDTO userUpdate, @MappingTarget Client client);
}
