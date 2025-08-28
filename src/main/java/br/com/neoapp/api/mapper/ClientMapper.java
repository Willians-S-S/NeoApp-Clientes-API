package br.com.neoapp.api.mapper;

import br.com.neoapp.api.controller.dto.ClientRequestDTO;
import br.com.neoapp.api.controller.dto.ClientResponseDTO;
import br.com.neoapp.api.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {LocalDate.class, OffsetDateTime.class, Period.class}
)
public interface ClientMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creatAt", expression = "java(now())")
    @Mapping(target = "updateAt", expression = "java(now())")
    Client toEntity(ClientRequestDTO clientRequestDTO);

    @Mapping(target = "age", expression = "java(calculateAge(client.getBirthday()))")
    ClientResponseDTO toResponse(Client client);

    default Integer calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    default OffsetDateTime now(){
        return  OffsetDateTime.now();
    }
}
