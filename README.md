# API-Biblioteca

API REST desenvolvida com Spring Boot para gerenciamento de uma biblioteca.

O sistema permite cadastro de livros e categorias, autenticação de usuários através de JWT e gerenciamento de empréstimos de livros.

Este projeto foi desenvolvido com foco em boas práticas de desenvolvimento back-end, segurança, testes unitários e modelagem de banco de dados.

## Tecnologias

- Java 17
- Spring Boot
- Spring Security
- JWT Authentication
- PostgreSQL
- Docker
- JPA / Hibernate
- MapStruct
- JUnit 5
- Mockito
- Swagger OpenAPI

## Funcionalidades

### Usuários

- Cadastro de usuários
- Login com JWT
- Controle de acesso por roles

### Livros

- Cadastro de livros
- Atualização de livros
- Exclusão de livros
- Consulta de livros

### Categorias

- Cadastro de categorias
- Atualização de categorias
- Consulta de categorias

### Empréstimos

- Realizar empréstimo de livros
- Devolver livros
- Consultar empréstimos

### Segurança

- Autenticação JWT
- Autorização baseada em roles
- Endpoints protegidos

## Regras de negócio

- Apenas administradores podem cadastrar livros.
- Apenas administradores podem cadastrar categorias.
- Um livro só pode ser emprestado, atualizado ou deletado se estiver disponível.
- Quando um empréstimo é criado, o livro torna-se indisponível.
- Quando o livro é devolvido, volta a ficar disponível.
- Usuários comuns podem visualizar apenas seus próprios empréstimos.
- Administradores podem visualizar todos os empréstimos.

## Como executar

### Clonar projeto

git clone https://github.com/Kaique-Nobre/API-Biblioteca

### Subir banco de dados

docker-compose up -d

### Executar aplicação

mvn spring-boot:run

## Credenciais de teste

Administrador

email: admin@library.com

senha: admin

## Documentação

Swagger UI:

http://localhost:8080/swagger-ui/index.html
