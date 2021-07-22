# MyApp

## Sobre
Projeto desenvolvido com o intuito implementar novas ferramentas decorrentes de estudos recentes.
## Infraestrutura
Nesta aplicação fizemos o deploy da API no Heroku, onde podemos testas os endpoints com a collections disponibilizada na pasta `docs`.

Alem disso, documentamos a aplicação com o `Open API e Swagger`, onde disponibilizamos o arquivo `swagger.yaml` na pasta `docs`.

#### PostgreSQL 
Essa aplicação conta com o banco de dados PostgreSQL, onde roda a nossa aplicação em produção e em desenvolvimento.
 
#### H2
Essa aplicação faz uso do banco de dados H2 para rodar os testes de integração. 

No arquivo `application.properties` a chave `APP_PROFILE` ja está configurada para rodar com profile de testes.

## Tecnologias

Aplicação
- Java 8
- Spring Boot
- Spring Web
- Spring Data
- Lombok
- Springfox Swagger
- Open API
- Rest Assured
- Validation API

Infra
- Database PostgreSQL
- Database H2

Cloud
- Heroku


