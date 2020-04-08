package br.com.orlandoburli.livraria.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReservaDto {

	private Long id;

	private UsuarioDto usuario;

	private LivroDto livro;

	private LocalDate dataReserva;
}