package br.com.orlandoburli.livraria.exceptions.usuario;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class UsuarioNaoInformadoException extends UsuarioException {

	private static final long serialVersionUID = 1L;

	public UsuarioNaoInformadoException(String message) {
		super(message);
	}
}