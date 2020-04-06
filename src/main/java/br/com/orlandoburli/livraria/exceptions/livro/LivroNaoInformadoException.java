package br.com.orlandoburli.livraria.exceptions.livro;

public class LivroNaoInformadoException extends LivroException {

	private static final long serialVersionUID = 1L;

	public LivroNaoInformadoException(String message) {
		super(message);
	}
}