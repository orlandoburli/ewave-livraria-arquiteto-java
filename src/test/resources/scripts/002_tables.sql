CREATE SEQUENCE livraria.seq_instituicao_ensino START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE livraria.seq_usuario START WITH 1 INCREMENT BY 1;

CREATE TABLE livraria.instituicao_ensino (
	id number(8) not null,
	nome varchar(100),
	endereco varchar(100),
	telefone varchar(11),
	cnpj char(14),
	status char(1) not null,
	CONSTRAINT pk_instituicao_ensino PRIMARY KEY (id)
);

CREATE TABLE livraria.usuario (
	id number(8) not null,
	nome varchar(100),
	endereco varchar(100),
	telefone varchar(11),
	email varchar(200),
	cpf char(11),
	instituicao_id number(8),
	status char(1) not null,
	CONSTRAINT pk_usuario PRIMARY KEY (id),
	CONSTRAINT fk_usuario_instituicao_ensino FOREIGN KEY (instituicao_id) REFERENCES livraria.instituicao_ensino(id)
);