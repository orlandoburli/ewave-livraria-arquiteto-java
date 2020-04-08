package br.com.orlandoburli.livraria.dto;

import java.time.LocalDate;

import br.com.orlandoburli.livraria.enums.StatusEmprestimo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@ApiModel(value = "Empréstimo", description = "Dados do empréstimo de um livro")
public class EmprestimoDto {

	@ApiModelProperty(notes = "Id do empréstimo", position = 1)
	private Long id;

	@ApiModelProperty(notes = "Usuário que realizou o empréstimo", position = 2)
	private UsuarioDto usuario;

	@ApiModelProperty(notes = "Livro que foi emprestado", position = 3)
	private LivroDto livro;

	@ApiModelProperty(notes = "Data do empréstimo", position = 4)
	private LocalDate dataEmprestimo;

	@ApiModelProperty(notes = "Data da devolução", position = 5)
	private LocalDate dataDevolucao;

	@ApiModelProperty(notes = "Status do empréstimo", position = 6)
	private StatusEmprestimo status;

	@ApiModelProperty(notes = "Data prevista / limite para devolução", position = 7)
	private LocalDate dataPrevistaDevolucao;
}
