package br.com.orlandoburli.livraria.exceptions.instituicaoensino;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class InstituicaoEnsinoNaoInformadaException extends InstituicaoEnsinoException {

	private static final long serialVersionUID = 1L;

	public InstituicaoEnsinoNaoInformadaException(String message) {
		super(message);
	}
}