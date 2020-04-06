package br.com.orlandoburli.livraria.model;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.orlandoburli.livraria.constraints.annotations.DataDevolucaoEmprestimo;
import br.com.orlandoburli.livraria.enums.StatusEmprestimo;
import br.com.orlandoburli.livraria.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "emprestimo", schema = Constants.SCHEMA)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DataDevolucaoEmprestimo
public class Emprestimo implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String SEQUENCE_NAME = "seq_emprestimo";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
	@SequenceGenerator(sequenceName = SEQUENCE_NAME, name = SEQUENCE_NAME, schema = Constants.SCHEMA, initialValue = 1, allocationSize = 1)
	private Long id;

	@NotNull(message = "{javax.validation.emprestimo.usuario.notNull}")
	private Usuario usuario;

	@NotNull(message = "{javax.validation.emprestimo.livro.notNull}")
	private Livro livro;

	@NotNull(message = "{javax.validation.emprestimo.dataEmprestimo.notNull}")
	private LocalDate dataEmprestimo;

	private LocalDate dataDevolucao;
	
	@NotNull(message = "{javax.validation.emprestimo.status.notNull}")
	private StatusEmprestimo status;
}