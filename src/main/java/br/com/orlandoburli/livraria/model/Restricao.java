package br.com.orlandoburli.livraria.model;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.orlandoburli.livraria.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "restricao", schema = Constants.SCHEMA)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restricao implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String SEQUENCE_NAME = "seq_restricao";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
	@SequenceGenerator(sequenceName = SEQUENCE_NAME, name = SEQUENCE_NAME, schema = Constants.SCHEMA, initialValue = 1, allocationSize = 1)
	private Long id;

	@NotNull(message = "{javax.validation.restricao.emprestimo.notNull}")
	@ManyToOne
	private Emprestimo emprestimo;

	@NotNull(message = "{javax.validation.restricao.restritoAte.notNull}")
	@Column(name = "restrito_ate")
	private LocalDate restritoAte;
}
