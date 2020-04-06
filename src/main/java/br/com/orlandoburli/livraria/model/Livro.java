package br.com.orlandoburli.livraria.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import br.com.orlandoburli.livraria.enums.Status;
import br.com.orlandoburli.livraria.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "livro", schema = Constants.SCHEMA)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Livro implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String SEQUENCE_NAME = "seq_livro";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
	@SequenceGenerator(sequenceName = SEQUENCE_NAME, name = SEQUENCE_NAME, schema = Constants.SCHEMA, initialValue = 1, allocationSize = 1)
	private Long id;

	@NotBlank(message = "{javax.validations.livro.titulo.notBlank}")
	@Size(min = 3, max = 100, message = "{javax.validations.livro.titulo.size}")
	private String titulo;

	@NotBlank(message = "{javax.validations.livro.genero.notBlank}")
	@Size(min = 3, max = 100, message = "{javax.validations.livro.genero.size}")
	private String genero;

	@NotBlank(message = "{javax.validations.livro.autor.notBlank}")
	@Size(min = 3, max = 100, message = "{javax.validations.livro.autor.size}")
	private String autor;
	
	@Size(max = 1000, message = "{javax.validations.livro.sinopse.size}")
	private String sinopse;
	
	private Status status;
}