package br.com.orlandoburli.livraria.exceptions.livro;

public class CapaNaoInformadaException extends LivroException {

	private static final long serialVersionUID = 1L;

	public CapaNaoInformadaException(String message) {
		super(message);
	}
}