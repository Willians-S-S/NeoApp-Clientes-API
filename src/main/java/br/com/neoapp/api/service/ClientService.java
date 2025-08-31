package br.com.neoapp.api.service;

import br.com.neoapp.api.controller.dto.ClientRequestDTO;
import br.com.neoapp.api.controller.dto.ClientResponseDTO;
import br.com.neoapp.api.exceptions.CpfExistsException;
import br.com.neoapp.api.exceptions.EmailExistsException;
import br.com.neoapp.api.mapper.ClientMapper;
import br.com.neoapp.api.model.Client;
import br.com.neoapp.api.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientMapper clientMapper;

    /**
     * Cria um novo cliente no sistema a partir dos dados fornecidos.
     *
     * <p>Este método executa as seguintes etapas:</p>
     * <ol>
     * <li><b>Validação de E-mail:</b> Verifica se o e-mail fornecido já existe no repositório.</li>
     * <li><b>Validação de CPF:</b> Verifica se o CPF fornecido já existe no repositório.</li>
     * <li><b>Mapeamento e Persistência:</b> Se as validações passarem, o DTO de requisição é convertido para uma entidade {@code Client}.</li>
     * <li><b>Salvamento:</b> A nova entidade de cliente é salva no banco de dados.</li>
     * <li><b>Retorno:</b> A entidade salva é mapeada para um DTO de resposta e retornada.</li>
     * </ol>
     *
     * @param clientRequestDTO O objeto de transferência de dados (DTO) que contém as informações do novo cliente a ser criado.
     * @return Um {@code ClientResponseDTO} contendo os dados do cliente recém-criado, incluindo seu ID gerado pelo banco de dados.
     * @throws EmailExistsException Se o e-mail fornecido em {@code clientRequestDTO} já estiver registrado no sistema.
     * @throws CpfExistsException Se o CPF fornecido em {@code clientRequestDTO} já estiver registrado no sistema.
     */
    public ClientResponseDTO creatClient(ClientRequestDTO clientRequestDTO) {
        if (clientRepository.existsByEmail(clientRequestDTO.email())){
            throw new EmailExistsException("O endereço de e-mail informado já está registrado.");
        }

        if (clientRepository.existsByCpf(clientRequestDTO.cpf())){
            throw new CpfExistsException("O CPF informado já está registrado.");
        }

        Client client = clientMapper.toEntity(clientRequestDTO);
        client = clientRepository.save(client);
        return clientMapper.toResponse(client);
    }


}
