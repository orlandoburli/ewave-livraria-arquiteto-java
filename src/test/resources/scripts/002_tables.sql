CREATE SEQUENCE livraria.seq_instituicao_ensino START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE livraria.seq_usuario START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE livraria.seq_livro START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE livraria.seq_emprestimo START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE livraria.seq_restricao START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE livraria.seq_reserva START WITH 1 INCREMENT BY 1;

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

CREATE TABLE livraria.livro (
	id number(8) not null,
	titulo varchar(100),
	genero varchar(100),
	autor varchar(100),
	sinopse varchar(1000),
	status char(1) not null,
	CONSTRAINT pk_livro PRIMARY KEY (id)
);

CREATE TABLE livraria.capa (
	id number(8) not null,
	imagem blob,
	CONSTRAINT pk_capa PRIMARY KEY (id),
	CONSTRAINT fk_capa_livro FOREIGN KEY (id) REFERENCES livraria.capa (id)
);

CREATE TABLE livraria.emprestimo (
	id number(8) not null,
	usuario_id number(8) not null,
	livro_id number(8) not null,
	data_emprestimo date not null,
	data_devolucao date,
	status char(1) not null,
	CONSTRAINT pk_emprestimo PRIMARY KEY (id),
	CONSTRAINT fk_emprestimo_usuario FOREIGN KEY (usuario_id) REFERENCES livraria.usuario(id),
	CONSTRAINT fk_emprestimo_livro FOREIGN KEY (livro_id) REFERENCES livraria.livro(id)
	
);

CREATE TABLE livraria.restricao (
	id number(8) not null,
	emprestimo_id number(8) not null,
	restrito_ate date,
	CONSTRAINT pk_restricao PRIMARY KEY (id),
	CONSTRAINT fk_restricao_emprestimo FOREIGN KEY (emprestimo_id) REFERENCES livraria.emprestimo(id)
);

CREATE TABLE livraria.reserva (
	id number(8) not null,
	usuario_id number(8) not null,
	livro_id number(8) not null,
	data_reserva date not null,
	CONSTRAINT pk_reserva PRIMARY KEY (id),
	CONSTRAINT fk_reserva_usuario FOREIGN KEY (usuario_id) REFERENCES livraria.usuario(id),
	CONSTRAINT fk_reserva_livro FOREIGN KEY (livro_id) REFERENCES livraria.livro(id)
);