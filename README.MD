# Projeto Livraria

Este projeto foi criado com o intuito de participar de um teste para a eWave.

## Arquitetura


### Versão Java

Foi utilizado o OpenJDK 11 para o desenvolvimento deste teste.

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

## CI/CD

Foi configurado o CircleCI para build automatizada deste projeto, sendo seus artefatos armazenados na própria ferramenta.

## Testes automatizados / Code Coverage

O objetivo é atingir uma cobertura de testes o mais próximo possível dos **100%**. Abaixo alguns resultados medidos durante o desenvolvimento deste teste.

## Code Quality

Foi atingida a nota **"A"** de qualidade de código, eliminando todos os possíveis bug's analisados pelo sonarqube, bem como os possíveis *code smells* detectados.
