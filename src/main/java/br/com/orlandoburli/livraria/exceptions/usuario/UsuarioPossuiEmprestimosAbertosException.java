package br.com.orlandoburli.livraria.exceptions.usuario;

public class UsuarioPossuiEmprestimosAbertosException extends UsuarioException {

	private static final long serialVersionUID = 1L;

	public UsuarioPossuiEmprestimosAbertosException(final String message) {
		super(message);
	}
}