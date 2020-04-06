package br.com.orlandoburli.livraria.dto;

import br.com.orlandoburli.livraria.enums.Status;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LivroDto {

	private Long id;

	private String titulo;

	private String genero;

	private String autor;
	
	private String sinopse;
	
	private Status status;
}