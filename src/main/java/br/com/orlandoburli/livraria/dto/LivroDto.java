package br.com.orlandoburli.livraria.dto;

import br.com.orlandoburli.livraria.enums.Status;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@ApiModel(value = "Livro", description = "Livros para serem emprestados")
public class LivroDto {

	@ApiModelProperty(notes = "Id do livro", position = 1)
	private Long id;

	@ApiModelProperty(notes = "Nome do livro", position = 2)
	private String titulo;

	@ApiModelProperty(notes = "Gênero do livro: ação, romance, terror, etc.", position = 3)
	private String genero;

	@ApiModelProperty(notes = "Nome do autor do livro", position = 4)
	private String autor;

	@ApiModelProperty(notes = "Sinopse do livro", position = 5)
	private String sinopse;

	@ApiModelProperty(notes = "Status do cadastro do livro", position = 6)
	private Status status;
}