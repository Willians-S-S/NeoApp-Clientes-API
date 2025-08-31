package br.com.neoapp.api.mapper;

import br.com.neoapp.api.controller.dto.ClientUpdateDTO;
import br.com.neoapp.api.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClientUpdateMapper {
    void updateToClient(ClientUpdateDTO userUpdate, @MappingTarget Client client);
}
