

# NeoApp Clientes API

API REST desenvolvida como **MVP** para o desafio técnico da **NeoApp**, focada no cadastro e gerenciamento de clientes pessoa física. O projeto foi construído com **Spring Boot**, aplicando boas práticas de design de APIs, padrões de projeto, segurança com **JWT** e cobertura de testes.

Deploy disponível em: [neoapp-clientes-api.onrender.com](https://neoapp-clientes-api.onrender.com)

---

## Objetivo do Projeto

Fornecer uma API segura e escalável para o gerenciamento de clientes, permitindo **inclusão, edição, listagem, busca e exclusão** de registros.  
A aplicação também calcula automaticamente a idade a partir da data de nascimento e expõe documentação interativa via **Swagger**.

---

## Funcionalidades

- Inclusão de novos clientes (`/auth/sign`)

- Atualização de clientes existentes (`/clients/{id}`)

- Exclusão de clientes (`/clients/{id}`)

- Listagem paginada de clientes (`/clients?page=0&size=10`)

- Busca por atributos (nome, e-mail, CPF, telefone, data de nascimento)

- Cálculo automático da idade do cliente

- Autenticação e autorização com **Spring Security + JWT**

- Documentação da API com **Swagger/OpenAPI**

- Tratamento de erros padronizado (exceções personalizadas)

- **Usuário administrador padrão criado automaticamente**



## Processo de Desenvolvimento

Minha abordagem foi estruturada em fases, começando com a fundação do projeto e adicionando camadas de funcionalidade, robustez e segurança de forma incremental.

---

### Fase 1: Fundação e Estrutura do Projeto

O ponto de partida foi estabelecer uma base sólida para a aplicação.

#### Configuração Inicial

- Iniciei o projeto com **Spring Initializr**, selecionando as dependências essenciais:
  - Spring Web
  - Spring Data JPA
  - Banco de dados em memória (**H2**)
- O H2 foi escolhido para simplificar a configuração do ambiente de desenvolvimento e facilitar os testes, eliminando a necessidade de um banco de dados externo para este MVP.

#### Modelo de Dados (Client)

- A primeira etapa de codificação foi a criação da entidade `Client`, definindo os atributos cadastrais básicos.
- Uma decisão importante foi separar a camada de dados (entidade) da camada de exposição (API) desde o início, planejando o uso de **DTOs (Data Transfer Objects)**.

#### Padrão DTO e Mappers

- Criei os seguintes DTOs:
  - `ClientRequestDTO` (para entrada de dados)
  - `ClientResponseDTO` (para saída)
- Isso segue a boa prática de não expor as entidades JPA diretamente na API, o que me dá controle sobre quais dados são enviados e recebidos, além de evitar acoplamento.
- Para a conversão entre DTOs e a entidade, implementei uma classe `ClientMapper`, centralizando essa lógica e mantendo os serviços mais limpos.

---

### Fase 2: Implementação do CRUD Básico

Com a base definida, o foco passou a ser a implementação das funcionalidades essenciais de um cadastro, conforme solicitado no desafio.

#### Endpoints e Lógica de Negócio

- Desenvolvi a camada de **Controller** para expor os endpoints REST e a de **Service** para orquestrar a lógica de negócio.
- Comecei pelo `POST /clients` para a criação de clientes, garantindo que as verificações de unicidade (**CPF** e **e-mail**) fossem tratadas.

#### Tratamento de Exceções Personalizado

- Em vez de deixar o Spring lançar exceções genéricas, criei exceções personalizadas:
  - `CpfAlreadyExistsException`
  - `EmailAlreadyExistsException`
- Para centralizar o tratamento, implementei um `GlobalExceptionHandler` (`@ControllerAdvice`).
- Isso permite retornar respostas de erro padronizadas e claras para o consumidor da API, melhorando a experiência de quem for integrar com o sistema.

#### CRUD Completo

- Implementei os demais endpoints:
  - `GET /clients/{id}`: Busca de cliente por ID, incluindo o tratamento para o caso de o cliente não ser encontrado (com a exceção `ClientNotFoundException`).
  - `PUT /clients/{id}`: Atualização de um cliente existente.
  - `DELETE /clients/{id}`: Exclusão de um cliente.

---

### Fase 3: Validação, Documentação e Funcionalidades Avançadas

Com o CRUD funcional, o próximo passo foi refinar a API, adicionando validações, documentação e as funcionalidades de busca paginada.

#### Validação de Dados

- Utilizei o **Spring Boot Starter Validation** para adicionar anotações de validação:
  - `@NotBlank`
  - `@Email`
  - Entre outras
- As validações foram aplicadas diretamente no `ClientRequestDTO`, garantindo que dados inválidos sejam rejeitados na camada de entrada, antes de chegarem à lógica de negócio.
- Criei uma validação personalizada `@PhoneNumber` para validar números de telefone, demonstrando a extensibilidade do framework.

#### Documentação com OpenAPI (Swagger)

- Integrei a dependência `springdoc-openapi-ui` para gerar a documentação da API automaticamente.
- Documentei cada endpoint com anotações:
  - `@Operation`
  - `@ApiResponse`
- As anotações explicam:
  - O que cada endpoint faz
  - Quais parâmetros aceita
  - Quais respostas pode retornar
- Essa documentação é crucial para a usabilidade e manutenção da API.

#### Listagem Paginada e Buscas

- Implementei o endpoint `GET /clients` para listagem paginada, utilizando os recursos do **Spring Data JPA** com `Pageable`.
- Adicionei também um endpoint para busca por atributos, permitindo filtrar os clientes por qualquer um de seus campos cadastrais, o que confere grande flexibilidade à API.

---

### Fase 4: Estratégia de Testes

Os testes foram uma prioridade desde o início. Adotei uma abordagem de testes em camadas para garantir a qualidade e a confiabilidade do código.

#### Testes Unitários

- Para a camada de serviço (`ClientService`), criei testes unitários com **Mockito**.
- Objetivo: validar a lógica de negócio de forma isolada.
- Cenários testados:
  - Regras de criação de cliente
  - Verificações de existência de e-mail/CPF

#### Testes de Integração

- Utilizei `@SpringBootTest` com **Testcontainers** ou **H2** para testar as camadas de **Controller** e **Repository**.
- Objetivo: validar o fluxo completo da aplicação, desde a requisição HTTP até a persistência no banco de dados.
- Cenários cobertos:
  - Sucesso
  - Erros de validação (ex: CPF inválido, e-mail já existente)
  - Regras de negócio

---

### Fase 5: Implementação de Segurança com Spring Security e JWT

Esta foi a última grande fase, focada em proteger a API.

#### Configuração do Spring Security

- Adicionei as dependências:
  - **Spring Security**
  - **OAuth2 Resource Server**
- Decisão inicial: criptografar as senhas dos usuários usando um `PasswordEncoder` (`BCryptPasswordEncoder`).
- O `PasswordEncoder` foi injetado como um **Bean** na aplicação.

#### Autenticação e Autorização

- Criei a entidade `Role` para gerenciar os papéis dos usuários (ex: `ROLE_USER`, `ROLE_ADMIN`) e a relacionei com a entidade `Client`.
- Implementei a interface `UserDetailsService` do Spring Security para carregar os dados do cliente a partir do banco de dados durante o processo de autenticação.
- Criei dois serviços dedicados:
  - `AuthenticationService`: lógica de autenticação
  - `TokenService`: geração e validação de tokens JWT

#### Endpoints de Autenticação

- Criei um `AuthController` separado para lidar com rotas públicas:
  - Registro de novos clientes: `/auth/sign`
  - Login: `/auth/login`
- O endpoint de criação de cliente foi movido para este controller, pois é uma ação que deve ser permitida sem autenticação prévia.
- O endpoint de login foi configurado para o método `POST`.
- Em caso de falha, retorna o status `401 Unauthorized`, conforme as boas práticas de REST.

#### Proteção de Endpoints

- Configurei o `SecurityFilterChain` para definir as regras de acesso:
  - Permitir acesso público aos endpoints de autenticação
  - Exigir um token JWT válido para todos os outros endpoints de gerenciamento de clientes:
    - Listar
    - Atualizar
    - Deletar

#### Atualização dos Testes

- Refatorei os testes de integração existentes para que eles pudessem:
  - Se autenticar
  - Incluir o token JWT nas requisições
- Objetivo: garantir que a cobertura de testes permanecesse alta mesmo após a implementação da camada de segurança.

## Tecnologias Utilizadas

- **Java 21**

- **Spring Boot 3**
  
  - Spring Web
  
  - Spring Data JPA
  
  - Spring Security

- **H2 Database** (ambiente de desenvolvimento)

- **Swagger / OpenAPI** (documentação)

- **JWT** (autenticação)

- **JUnit & Mockito** (testes)

- **Docker**  (deploy)

---

## Estrutura do Projeto

```
src/main/java/br/com/neoapp/api
│
├── config             # Inicialização de dados e configurações
├── controller         # Controladores REST (endpoints)
│   └── dto            # Data Transfer Objects (DTOs)
├── exceptions         # Exceções customizadas e handler global
├── mapper             # Conversores entre entidades e DTOs
├── model              # Entidades JPA (Client, Role)
├── repository         # Interfaces JPA
├── security           # Configuração de segurança e JWT
└── service            # Regras de negócio (ClientService, AuthService)
```

---

## ## Autenticação

A API utiliza **JWT**.

### Usuário Administrador Padrão

Um usuário **ADMIN** é criado automaticamente na inicialização do projeto, permitindo acesso imediato aos recursos administrativos:

- **E-mail:** `admin@email.com`

- **Senha:** `senhaforte`

### Fluxo de Autenticação

1. Crie um novo cliente via:
   
   ```http
   POST /api/v1/auth/sign
   ```

2. Realize login:
   
   ```http
   POST /api/v1/auth/login
   ```
   
   Exemplo de resposta:
   
   ```json
   {
    "accessToken": "eyJhbGciOiJIUzI1...",
    "expiresIn": 3600
   }
   ```

3. Use o token no cabeçalho das requisições:
   
   ```
   Authorization: Bearer <seu-token-jwt>
   ```

---

## Exemplos de Uso:

 Abaixo estão **exemplos completos de requisições (curl)** para todos os endpoints do sistema, utilizando a URL base: `https://neoapp-clientes-api.onrender.com`. Como o deploy foi realizado na camada gratuita do Render, houve momentos em que a aplicação entrou em modo de hibernação por inatividade. Nesses casos, a primeira requisição após um período de dormência pode levar até alguns minutos para ser processada, enquanto o serviço é reinicializado.

Os exemplos incluem **todos os endpoints** dos controllers `AuthController` e `ClientController`, com explicações, headers necessários (como JWT), e exemplos de corpo e resposta.

---

## URL Base da API

```
https://neoapp-clientes-api.onrender.com/api/v1
```

---

# 1. Autenticação (`/auth`)

## 1.1 POST `/auth/login` – Autenticar usuário

**Descrição:** Gera um token JWT com base em email e senha.

```bash
curl -X POST 'https://neoapp-clientes-api.onrender.com/api/v1/auth/login' \

-H 'Content-Type: application/json' \

-d '{

"email": "cliente@example.com",

"password": "senha123"

}'
```

### Exemplo de Resposta (200 OK):

```json
{

"token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.xxxxx",

"type": "Bearer"

}
```

> Guarde o token JWT para usar nos próximos endpoints protegidos.

---

## 1.2 POST `/auth/sign` – Criar novo cliente

**Descrição:** Cria um novo cliente (sem necessidade de autenticação).

```bash
curl -X POST 'https://neoapp-clientes-api.onrender.com/api/v1/auth/sign' \

-H 'Content-Type: application/json' \

-d '{

"name": "Ana Silva",

"email": "ana.silva@example.com",

"password": "senhaSegura123",

"cpf": "12345678909",

"phone": "+5511999998888",

"birthday": "1990-05-15"

}'
```

### Resposta esperada (201 Created):

- Status: `201 Created`

- Header: `Location: https://neoapp-clientes-api.onrender.com/api/v1/clients/{id}`

- Corpo:

```json
{

"id": "abc123-def456",

"name": "Ana Silva",

"email": "ana.silva@example.com",

"cpf": "12345678909",

"phone": "+5511999998888",

"birthday": "1990-05-15"

}
```

> Se o CPF ou e-mail já existir: `409 Conflict`

---

# 2. Clientes (`/clients`)

> **Todos os endpoints abaixo exigem autenticação com Bearer Token JWT.**

Use o token obtido no `/login`:

```bash
-H 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.xxxxx'
```

---

## 2.1 GET `/clients` – Listar todos os clientes (com paginação)

**Descrição:** Retorna todos os clientes com paginação.

```bash
curl -X GET 'https://neoapp-clientes-api.onrender.com/api/v1/clients?page=0&size=10&sort=name,asc' \

-H 'Authorization: Bearer <SEU_TOKEN_JWT>'
```

### Parâmetros opcionais:

- `page`: número da página (default: 0)

- `size`: quantidade por página (default: 20)

- `sort`: campo e ordem (ex: `name,asc`, `birthday,desc`)

### Exemplo de resposta (200 OK):

```json
{

"content": [

{

"id": "abc123",

"name": "Ana Silva",

"email": "ana.silva@example.com",

"cpf": "12345678909",

"phone": "+5511999998888",

"birthday": "1990-05-15"

}

],

"totalElements": 1,

"totalPages": 1,

"number": 0,

"size": 10

}
```

---

## 2.2 GET `/clients/{id}` – Buscar cliente por ID

```bash
curl -X GET 'https://neoapp-clientes-api.onrender.com/api/v1/clients/abc123-def456' \

-H 'Authorization: Bearer <SEU_TOKEN_JWT>'
```

### Resposta esperada (200 OK):

```json
{

"id": "abc123-def456",

"name": "Ana Silva",

"email": "ana.silva@example.com",

"cpf": "12345678909",

"phone": "+5511999998888",

"birthday": "1990-05-15"

}
```

> Se não existir: `404 Not Found` com corpo `ClientNotFound`

---

## 2.3 PUT `/clients/{id}` – Atualizar cliente

```bash
curl -X PUT 'https://neoapp-clientes-api.onrender.com/api/v1/clients/abc123-def456' \

-H 'Authorization: Bearer <SEU_TOKEN_JWT>' \

-H 'Content-Type: application/json' \

-d '{

"name": "Ana Silva Atualizada",

"email": "ana.atualizada@example.com",

"phone": "+5511988887777",

"birthday": "1990-05-15"

}'
```

> O campo `cpf` não pode ser alterado.

### Resposta esperada (200 OK):

```json
{

"id": "abc123-def456",

"name": "Ana Silva Atualizada",

"email": "ana.atualizada@example.com",

"cpf": "12345678909",

"phone": "+5511988887777",

"birthday": "1990-05-15"

}
```

> Erros:

- `404`: cliente não encontrado

- `422`: dados inválidos (ex: email mal formatado)

---

## 2.4 DELETE `/clients/{id}` – Excluir cliente

```bash
curl -X DELETE 'https://neoapp-clientes-api.onrender.com/api/v1/clients/abc123-def456' \

-H 'Authorization: Bearer <SEU_TOKEN_JWT>'
```

### Resposta esperada (204 No Content):

- Sem corpo

- Apenas status `204`

> Se ID não existir: `404 Not Found`

---

## 2.5 GET `/clients/attributes` – Buscar clientes por filtros

**Descrição:** Filtro dinâmico com múltiplos parâmetros opcionais.

```bash
curl -X GET 'https://neoapp-clientes-api.onrender.com/api/v1/clients/attributes?name=Ana&email=ana&birthdayStart=1990-01-01&birthdayEnd=1995-12-31&page=0&size=5' \

-H 'Authorization: Bearer <SEU_TOKEN_JWT>'
```

### Parâmetros opcionais:

- `name`: busca parcial (like)

- `email`: busca parcial

- `cpf`: exato

- `phone`: busca parcial

- `birthdayStart`: data inicial (AAAA-MM-DD)

- `birthdayEnd`: data final (AAAA-MM-DD)

- `page`, `size`, `sort`: paginação

### Exemplo de resposta (200 OK):

```json
{

"content": [

{

"id": "abc123",

"name": "Ana Silva",

"email": "ana.silva@example.com",

"cpf": "12345678909",

"phone": "+5511999998888",

"birthday": "1990-05-15"

}

],

"totalElements": 1,

"totalPages": 1,

"number": 0,

"size": 5

}
```

> 🔍 Se nenhum parâmetro for passado, retorna todos os clientes paginados.

---

## 2.6 GET `/clients/one-client-attributes` – Buscar um cliente único por atributos

**Descrição:** Localiza um cliente com base em combinação de campos (espera-se resultado único).

```bash
curl -X GET 'https://neoapp-clientes-api.onrender.com/api/v1/clients/one-client-attributes?email=ana.silva@example.com&cpf=12345678909' \

-H 'Authorization: Bearer <SEU_TOKEN_JWT>'
```

### Parâmetros (todos opcionais, mas combinados devem identificar um único cliente):

- `name`

- `email`

- `cpf`

- `phone`

- `birthday` (AAAA-MM-DD)

### Resposta esperada (200 OK):

```json
{

"id": "abc123-def456",

"name": "Ana Silva",

"email": "ana.silva@example.com",

"cpf": "12345678909",

"phone": "+5511999998888",

"birthday": "1990-05-15"

}
```

> Se não encontrar: `404 Not Found` com `StandardError`

---

## Documentação Swagger (OpenAPI)

Você pode visualizar a documentação interativa em:

[https://neoapp-clientes-api.onrender.com/swagger-ui.html](https://neoapp-clientes-api.onrender.com/swagger-ui.html)

Ou:

[https://neoapp-clientes-api.onrender.com/v3/api-docs](https://neoapp-clientes-api.onrender.com/v3/api-docs) (JSON)

## Testes

O projeto possui cobertura de testes:

- **Unitários** (Mockito) → regras de negócio

- **Integração** → fluxo completo (Controller + Repository)

- Cenários de sucesso, erros de validação e autenticação

Execute:

```bash
mvn test
```

---

## Contribuição

1. Faça um fork do projeto

2. Crie uma branch (`git checkout -b feature/nova-funcionalidade`)

3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)

4. Push (`git push origin feature/nova-funcionalidade`)

5. Abra um Pull Request

---

## Licença

Este projeto é distribuído sob a licença MIT. Consulte o arquivo LICENSE para mais detalhes.

---

## Contato

Desenvolvido por **Willians S.S.**  
Email: [willianssilva@ufpi.edu.br]([willianssilva@ufpi.edu.br](mailto:willianssilva@ufpi.edu.br))


