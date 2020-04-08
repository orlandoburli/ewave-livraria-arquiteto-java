package br.com.orlandoburli.livraria.dto;

import java.io.Serializable;

import br.com.orlandoburli.livraria.enums.Status;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@ApiModel(value = "Instituição de Ensino", description = "Instituições de Ensino cujos usuários podem emprestar livros")
public class InstituicaoEnsinoDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(name = "Id da instituição", position = 1)
	private Long id;

	@ApiModelProperty(name = "Nome da instituição", position = 2)
	private String nome;

	@ApiModelProperty(name = "Endereço da instituição", position = 3)
	private String endereco;

	@ApiModelProperty(name = "Cnpj da instituição, somente números", position = 4)
	private String cnpj;

	@ApiModelProperty(name = "Telefone da instituição, somente números", position = 5)
	private String telefone;

	@ApiModelProperty(name = "Status da instituição", position = 6)
	private Status status;
}