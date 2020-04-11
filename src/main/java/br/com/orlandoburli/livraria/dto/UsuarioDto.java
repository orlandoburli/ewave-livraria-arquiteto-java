package br.com.orlandoburli.livraria.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.orlandoburli.livraria.enums.Status;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@ApiModel(value = "Usuário", description = "Usuários que podem emprestar livros")
public class UsuarioDto {

	@ApiModelProperty(notes = "Id do usuário", position = 1)
	private Long id;

	@ApiModelProperty(notes = "Nome do usuário", position = 2)
	private String nome;

	@ApiModelProperty(notes = "Endereço do usuário", position = 3)
	private String endereco;

	@ApiModelProperty(notes = "Cpf do usuário, somente números", position = 4)
	private String cpf;

	@ApiModelProperty(notes = "Telefone do usuário, somente números", position = 5)
	private String telefone;

	@ApiModelProperty(notes = "Email do usuário", position = 6)
	private String email;

	@ApiModelProperty(notes = "Instituição à qual pertence o usuário", position = 7)
	private InstituicaoEnsinoDto instituicao;

	@ApiModelProperty(notes = "Status do usuário", position = 8)
	@JsonIgnore
	private Status status;
}