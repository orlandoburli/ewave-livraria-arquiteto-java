package br.com.orlandoburli.livraria.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class LivrariaException extends Exception {

	private static final long serialVersionUID = 1L;

	public LivrariaException(final String message) {
		super(message);
	}

	@Override
	@JsonIgnore
	public StackTraceElement[] getStackTrace() {
		return super.getStackTrace();
	}

	@Override
	@JsonIgnore
	public Throwable getCause() {
		return super.getCause();
	}
}