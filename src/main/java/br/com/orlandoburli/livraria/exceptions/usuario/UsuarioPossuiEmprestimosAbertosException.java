package br.com.orlandoburli.livraria.exceptions.usuario;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UsuarioPossuiEmprestimosAbertosException extends UsuarioException {

	private static final long serialVersionUID = 1L;

	public UsuarioPossuiEmprestimosAbertosException(final String message) {
		super(message);
	}
}