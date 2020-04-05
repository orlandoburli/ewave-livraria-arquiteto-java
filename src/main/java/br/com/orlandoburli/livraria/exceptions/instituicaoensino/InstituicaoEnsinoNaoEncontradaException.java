package br.com.orlandoburli.livraria.exceptions.instituicaoensino;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InstituicaoEnsinoNaoEncontradaException extends InstituicaoEnsinoException {

	private static final long serialVersionUID = 1L;

	public InstituicaoEnsinoNaoEncontradaException() {
		super("Instituição de ensino não encontrada");
	}
}