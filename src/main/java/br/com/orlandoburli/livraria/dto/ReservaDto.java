package br.com.orlandoburli.livraria.dto;

import java.time.LocalDate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@ApiModel(value = "Reserva", description = "Dados da reserva de um livro")
public class ReservaDto {

	@ApiModelProperty(notes = "Id da reserva", position = 1)
	private Long id;

	@ApiModelProperty(notes = "Usuário que realizou a reserva", position = 2)
	private UsuarioDto usuario;

	@ApiModelProperty(notes = "Livro que foi reservado", position = 3)
	private LivroDto livro;

	@ApiModelProperty(notes = "Data da reserva", position = 4)
	private LocalDate dataReserva;
}