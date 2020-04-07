package br.com.orlandoburli.livraria.exceptions.validations;

import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import br.com.orlandoburli.livraria.exceptions.LivrariaException;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ValidationLivrariaException extends LivrariaException {

	private static final long serialVersionUID = 1L;

	private final Map<String, Set<String>> errors;

	public ValidationLivrariaException(final String message, final Map<String, Set<String>> errors) {
		super(message);
		this.errors = errors;
	}

	public Map<String, Set<String>> getErrors() {
		return errors;
	}
}