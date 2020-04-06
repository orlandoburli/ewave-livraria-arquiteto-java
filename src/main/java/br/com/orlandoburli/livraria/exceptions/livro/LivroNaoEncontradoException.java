package br.com.orlandoburli.livraria.exceptions.livro;

public class LivroNaoEncontradoException extends LivroException {

	private static final long serialVersionUID = 1L;

	public LivroNaoEncontradoException(String message) {
		super(message);
	}
}