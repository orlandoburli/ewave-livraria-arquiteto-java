CREATE SEQUENCE livraria.seq_instituicao_ensino START WITH 1 INCREMENT BY 1;

CREATE TABLE livraria.instituicao_ensino (
	id number(8) not null,
	nome varchar(100),
	endereco varchar(100),
	telefone varchar(10),
	cnpj char(14),
	status char(1) not null,
	CONSTRAINT pk_instituicao_ensino PRIMARY KEY (id)
);