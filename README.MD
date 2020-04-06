# Projeto Livraria

Este projeto foi criado com o intuito de participar de um teste para a eWave.

[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-white.svg)](https://sonarcloud.io/dashboard?id=orlandoburli_ewave-livraria-arquiteto-java)

## Arquitetura


### Versão Java

Foi utilizado o OpenJ9 (JDK 11) para o desenvolvimento deste teste.

### Frameworks

Foi utilizado como base do projeto o Spring Boot 2.2.6. Algumas bibliotecas adicionais, da própria suite do spring:
  * **spring-boot-starter-data-jpa** - Core do Spring Boot para uso de JPA nos projetos
  * **spring-boot-starter-security** - Core do Spring Boot para segurança de API's
  * **spring-boot-starter-web** - Core do Spring Boot para API's 
  * **spring-boot-devtools** - Utilitários do Spring Boot para desenvolvedores
  * **org.postgresql** - Driver para postgres
  * **hibernate-validator** - Biblioteca adicional do Hibernate para adicionar os validadores JPA do Hibernate
  * **lombok** - Biblioteca utilitária para geração automática de getter's, setter's, builder's, entre outros.
  * **org.apache.commons (commons-lang3)** - Biblioteca da Apache, com diversas classes utilitárias
  * **commons-io** - Biblioteca utilitária para trabalhar com arquivos
  * **commons-codec** - Complementar ao *commons-io*, adicionar encoder's gerais.

Bibliotecas de teste:
  * **spring-boot-starter-test** - Core do Spring Boot para testes em microserviços.
  * **spring-security-test** - Utilitário do Spring Security para realizar testes unitários nas API's do Spring Boot.
  * **org.hamcrest** - Extensões das bibliotecas de Matcher's do JUnit (Já está embutido no spring-boot-starter-test)
  * **h2** - Driver do banco H2, utilizado para os testes unitários.
  * **javafaker** - Biblioteca utilitária para geração de dados aleatórios para testes.

### Definição de pacotes

### Modelo de dados
[![modelo](docs/Livraria.png)](https://dbdiagram.io/d/5e285bfd9e76504e0ef08a7c)

## CI/CD

Foi configurado o CircleCI para build automatizada deste projeto, sendo seus artefatos armazenados na própria ferramenta.

https://app.circleci.com/pipelines/github/orlandoburli/ewave-livraria-arquiteto-java

[![orlandoburli](https://circleci.com/gh/orlandoburli/ewave-livraria-arquiteto-java.svg?style=shield)](https://circleci.com/gh/orlandoburli/ewave-livraria-arquiteto-java.svg?style=shield)

## Testes automatizados

Neste projeto, escrevemos os testes em dois níveis: *Service* e *Controller*. 

Em *Service*, testamos unitariamente cada um dos serviços em detalhes de suas regras de negócio, incluindo as validações JPA, bem como seus retornos de mensagem.

Já em *Controller*, testamos as API's REST e suas fronteiras, testando o dado que entra, o dado que sai, e seus respectivos códigos HTTP. Fechando, desta maneira, todo o ciclo de API's.

Foi atingido o percentual de cobertura de [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=orlandoburli_ewave-livraria-arquiteto-java&metric=coverage)](https://sonarcloud.io/dashboard?id=orlandoburli_ewave-livraria-arquiteto-java). 

## Code Quality / Code Coverage

Neste projeto, configuramos o SonarCloud, que é o SonarQube na nuvem, que está gratuito para projetos open source. Podemos ver o resultado da qualidade do código no link abaixo.

https://sonarcloud.io/dashboard?id=orlandoburli_ewave-livraria-arquiteto-java

Foi atingida a nota [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=orlandoburli_ewave-livraria-arquiteto-java&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=orlandoburli_ewave-livraria-arquiteto-java) de qualidade de código, eliminando todos os possíveis bug's analisados pelo sonarqube, bem como os possíveis *code smells* detectados.


[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=orlandoburli_ewave-livraria-arquiteto-java&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=orlandoburli_ewave-livraria-arquiteto-java)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=orlandoburli_ewave-livraria-arquiteto-java&metric=bugs)](https://sonarcloud.io/dashboard?id=orlandoburli_ewave-livraria-arquiteto-java)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=orlandoburli_ewave-livraria-arquiteto-java&metric=code_smells)](https://sonarcloud.io/dashboard?id=orlandoburli_ewave-livraria-arquiteto-java)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=orlandoburli_ewave-livraria-arquiteto-java&metric=coverage)](https://sonarcloud.io/dashboard?id=orlandoburli_ewave-livraria-arquiteto-java)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=orlandoburli_ewave-livraria-arquiteto-java&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=orlandoburli_ewave-livraria-arquiteto-java)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=orlandoburli_ewave-livraria-arquiteto-java&metric=ncloc)](https://sonarcloud.io/dashboard?id=orlandoburli_ewave-livraria-arquiteto-java)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=orlandoburli_ewave-livraria-arquiteto-java&metric=alert_status)](https://sonarcloud.io/dashboard?id=orlandoburli_ewave-livraria-arquiteto-java)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=orlandoburli_ewave-livraria-arquiteto-java&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=orlandoburli_ewave-livraria-arquiteto-java)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=orlandoburli_ewave-livraria-arquiteto-java&metric=security_rating)](https://sonarcloud.io/dashboard?id=orlandoburli_ewave-livraria-arquiteto-java)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=orlandoburli_ewave-livraria-arquiteto-java&metric=sqale_index)](https://sonarcloud.io/dashboard?id=orlandoburli_ewave-livraria-arquiteto-java)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=orlandoburli_ewave-livraria-arquiteto-java&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=orlandoburli_ewave-livraria-arquiteto-java)
