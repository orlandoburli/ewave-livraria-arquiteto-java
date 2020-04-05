package br.com.orlandoburli.livraria.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.br.CPF;

import br.com.orlandoburli.livraria.enums.Status;
import br.com.orlandoburli.livraria.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario", schema = Constants.SCHEMA)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

	private static final String SEQUENCE_NAME = "seq_usuario";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
	@SequenceGenerator(sequenceName = SEQUENCE_NAME, name = SEQUENCE_NAME, schema = Constants.SCHEMA)
	private Long id;

	@NotBlank(message = "{javax.validations.usuario.nome.notBlank}")
	private String nome;

	@NotBlank(message = "{javax.validations.usuario.endereco.notBlank}")
	private String endereco;

	@CPF(message = "{javax.validations.usuario.cpf.invalid}")
	@NotBlank(message = "{javax.validations.usuario.cpf.notBlank}")
	private String cpf;

	@Pattern(regexp = "^[0-9]{10,11}$", message = "javax.validations.usuario.telefone.invalid")
	private String telefone;

	@Email(message = "{javax.validations.usuario.email.invalid}")
	@Size(max = 200, message = "{javax.validations.usuario.email.size}")
	private String email;

	@ManyToOne(optional = false)
	@NotNull(message = "{javax.validations.usuario.instituicao.notNull}")
	private InstituicaoEnsino instituicao;
	
	@NotNull(message = "{javax.validations.usuario.status.notNull}")
	private Status status;
}