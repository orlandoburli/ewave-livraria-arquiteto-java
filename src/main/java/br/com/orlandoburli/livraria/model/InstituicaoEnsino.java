package br.com.orlandoburli.livraria.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.br.CNPJ;

import br.com.orlandoburli.livraria.enums.Status;
import br.com.orlandoburli.livraria.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "instituicao_ensino", schema = Constants.SCHEMA)
@Getter 
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstituicaoEnsino {

	private static final String SEQUENCE_NAME = "seq_instituicao_ensino";
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
	@SequenceGenerator(sequenceName = SEQUENCE_NAME, name = SEQUENCE_NAME, schema = Constants.SCHEMA)
	private Long id;
	
	@NotBlank(message = "{javax.validations.instituicao.nome.notBlank}")
	@Size(max = 100, min = 3, message = "{javax.validations.instituicao.nome.size}")
	private String nome;
	
	@NotBlank(message = "{javax.validations.instituicao.endereco.notBlank}")
	@Size(max = 100, min = 3, message = "{javax.validations.instituicao.endereco.size}")
	private String endereco;
	
	@CNPJ(message = "{javax.validations.instituicao.cnpj.invalid}")
	@NotBlank(message = "{javax.validations.instituicao.cnpj.notBlank}")
	private String cnpj;
	
	@Pattern(regexp = "^[0-9]{10,11}$", message = "{javax.validations.instituicao.telefone.invalid}")
	private String telefone;
	
	@NotNull(message = "{javax.validations.instituicao.status.notNull}")
	private Status status;
}
