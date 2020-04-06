package br.com.orlandoburli.livraria.exceptions.livro;

import br.com.orlandoburli.livraria.exceptions.LivrariaException;

public class LivroException extends LivrariaException{

	private static final long serialVersionUID = 1L;

	public LivroException(String message) {
		super(message);
	}
}