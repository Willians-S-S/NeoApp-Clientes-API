package br.com.neoapp.api.service;

import br.com.neoapp.api.controller.dto.ClientRequestDTO;
import br.com.neoapp.api.controller.dto.ClientResponseDTO;
import br.com.neoapp.api.controller.dto.ClientUpdateDTO;
import br.com.neoapp.api.exceptions.ClientNotFound;
import br.com.neoapp.api.exceptions.CpfExistsException;
import br.com.neoapp.api.exceptions.EmailExistsException;
import br.com.neoapp.api.mapper.ClientMapper;
import br.com.neoapp.api.mapper.ClientUpdateMapper;
import br.com.neoapp.api.model.Client;
import br.com.neoapp.api.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private ClientUpdateMapper clientUpdateMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        client = clientRepository.save(client);
        return clientMapper.toResponse(client);
    }

    /**
     * Busca uma lista paginada de todos os clientes.
     * <p>
     * Este método consulta o repositório para obter uma página de entidades {@code Client}
     * aplicando os critérios de paginação e ordenação fornecidos. Em seguida, utiliza
     * um mapper para converter a página de entidades em uma página de {@code ClientResponseDTO},
     * que é um formato seguro para exposição em camadas superiores, como a API.
     *
     * @param pageable Objeto contendo as informações de paginação (número da página, tamanho)
     * e ordenação (campo, direção).
     * @return Uma {@link Page} contendo a lista de {@link ClientResponseDTO} para a página
     * solicitada, juntamente com todas as informações de paginação (total de elementos,
     * total de páginas, etc.).
     */
    public Page<ClientResponseDTO> getAllClientsPageable(Pageable pageable) {
        return clientMapper.toPageResponse(clientRepository.findAll(pageable));
    }

    /**
     * Busca um cliente específico pelo seu ID.
     * <p>
     * Este método consulta o repositório em busca de um cliente com o ID fornecido.
     * Se o cliente for encontrado, ele é mapeado para um objeto {@code ClientResponseDTO}.
     * Caso contrário, uma exceção {@code ClientNotFound} é lançada, indicando que o
     * recurso não existe.
     *
     * @param id O identificador único (ID) do cliente a ser buscado.
     * @return O {@link ClientResponseDTO} correspondente ao cliente encontrado.
     * @throws ClientNotFound se nenhum cliente for encontrado com o ID especificado.
     */
    public ClientResponseDTO getClientById(String id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFound("O clinte informado não foi encontrado."));

        return clientMapper.toResponse(client);
    }

    /**
     * Atualiza os dados de um cliente existente com base em seu ID.
     * <p>
     * Este método primeiro localiza o cliente no banco de dados. Se não for encontrado,
     * lança uma exceção {@code ClientNotFound}. Caso contrário, utiliza um mapper para
     * aplicar as atualizações do DTO na entidade encontrada, persiste as alterações
     * e retorna um DTO de resposta com os dados atualizados.
     *
     * @param id O identificador único (ID) do cliente a ser atualizado.
     * @param clientUpdateDTO O DTO contendo os novos dados para o cliente.
     * @return Um {@link ClientResponseDTO} com as informações do cliente após a atualização.
     * @throws ClientNotFound se nenhum cliente for encontrado com o ID especificado.
     */
    public ClientResponseDTO updateClientById(String id, ClientUpdateDTO clientUpdateDTO) {
        Client client = clientRepository.findById(id).orElseThrow(() -> new
                ClientNotFound("O clinte informado não foi encontrado."));

        clientUpdateMapper.updateToClient(clientUpdateDTO, client);

        clientRepository.save(client);

        return clientMapper.toResponse(client);
    }

    /**
     * Exclui um cliente do banco de dados com base no seu ID.
     * <p>
     * O método primeiro verifica a existência do cliente. Se o cliente for encontrado,
     * ele é removido permanentemente. Se não for encontrado, uma exceção
     * {@code ClientNotFound} é lançada.
     *
     * @param id O identificador único (ID) do cliente a ser excluído.
     * @throws ClientNotFound se nenhum cliente for encontrado com o ID especificado.
     */
    public void deleteClientById(String id) {
        Client client = clientRepository.findById(id).orElseThrow(() -> new
                ClientNotFound("O clinte informado não foi encontrado."));

        clientRepository.delete(client);
    }

    /**
     * Busca uma lista paginada de clientes aplicando filtros dinâmicos.
     * <p>
     * Este método repassa os critérios de filtro para a camada de repositório,
     * que constrói uma consulta dinâmica para buscar os clientes correspondentes.
     * Os clientes encontrados são então mapeados para DTOs de resposta.
     *
     * @param name            Nome parcial ou completo do cliente para filtrar.
     * @param email           Email exato do cliente para filtrar.
     * @param cpf             CPF exato do cliente para filtrar.
     * @param phone           Telefone exato do cliente para filtrar.
     * @param birthdayStart   A data inicial para o filtro de data de nascimento.
     * @param birthdayEnd     A data final para o filtro de data de nascimento.
     * @param pageable        Objeto que contém as informações de paginação e ordenação.
     * @return Uma {@link Page} de {@link ClientResponseDTO} contendo os clientes que
     */
    public Page<ClientResponseDTO> getAllClientsWithAttributesPage(
            String name,
            String email,
            String cpf,
            String phone,
            LocalDate birthdayStart,
            LocalDate birthdayEnd,
            Pageable pageable) {

        Page<Client> clients = clientRepository.getAllClientsWithAttributesPage(
                name,
                email,
                cpf,
                phone,
                birthdayStart,
                birthdayEnd,
                pageable);

        return clientMapper.toPageResponse(clients);
    }

    /**
     * Busca um único cliente que corresponda a uma combinação de atributos.
     * <p>
     * Este método consulta o repositório esperando que a combinação de filtros
     * retorne no máximo um cliente. Se nenhum cliente for encontrado, lança
     * uma exceção {@code ClientNotFound}.
     *
     * @param name     O nome do cliente a ser pesquisado.
     * @param email    O email do cliente a ser pesquisado.
     * @param cpf      O CPF do cliente a ser pesquisado.
     * @param phone    O telefone do cliente a ser pesquisado.
     * @param birthday A data de nascimento do cliente a ser pesquisada.
     * @return O {@link ClientResponseDTO} do cliente único encontrado.
     * @throws ClientNotFound se nenhum cliente for encontrado com a combinação de atributos fornecida.
     */
    public ClientResponseDTO getClientsWithAttributes(
            String name,
            String email,
            String cpf,
            String phone,
            LocalDate birthday) {
        Client client = clientRepository.findClientsWithAttributes(
                name,
                email,
                cpf,
                phone,
                birthday).orElseThrow(
                () -> new ClientNotFound("Os dados do cliente informado não foram encontrados. Verifique se as informações digitadas estão corretas.")
        );

        return clientMapper.toResponse(client);
    }
}
