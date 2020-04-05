package br.com.orlandoburli.livraria.exceptions.usuario;

import br.com.orlandoburli.livraria.exceptions.LivrariaException;

public class UsuarioException extends LivrariaException {

	private static final long serialVersionUID = 1L;

	public UsuarioException(String message) {
		super(message);
	}
}