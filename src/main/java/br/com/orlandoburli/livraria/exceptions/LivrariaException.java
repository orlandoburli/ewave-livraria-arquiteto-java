package br.com.orlandoburli.livraria.exceptions;

public abstract class LivrariaException extends Exception {

	private static final long serialVersionUID = 1L;

	public LivrariaException(String message) {
		super(message);
	}
}
