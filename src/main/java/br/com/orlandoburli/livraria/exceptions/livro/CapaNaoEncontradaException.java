package br.com.orlandoburli.livraria.exceptions.livro;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CapaNaoEncontradaException extends LivroException{

	private static final long serialVersionUID = 1L;

	public CapaNaoEncontradaException(String message) {
		super(message);
	}
}