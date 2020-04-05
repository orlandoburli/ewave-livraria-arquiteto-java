package br.com.orlandoburli.livraria.exceptions.instituicaoensino;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InstituicaoEnsinoComUsuariosException extends InstituicaoEnsinoException {

	private static final long serialVersionUID = 1L;

	public InstituicaoEnsinoComUsuariosException(String message) {
		super(message);
	}
}