package br.com.orlandoburli.livraria.exceptions.instituicaoensino;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)

public class CnpjJaExistenteException extends InstituicaoEnsinoException {

	private static final long serialVersionUID = 1L;

	public CnpjJaExistenteException(final String message) {
		super(message);
	}
}