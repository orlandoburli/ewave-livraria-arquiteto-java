package br.com.orlandoburli.livraria.dto;

import java.time.LocalDate;

import br.com.orlandoburli.livraria.enums.StatusEmprestimo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmprestimoDto {

	private Long id;

	private UsuarioDto usuario;

	private LivroDto livro;

	private LocalDate dataEmprestimo;

	private LocalDate dataDevolucao;

	private StatusEmprestimo status;

	private LocalDate dataPrevistaDevolucao;
}
