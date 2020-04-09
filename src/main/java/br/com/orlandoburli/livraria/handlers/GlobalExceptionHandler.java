package br.com.orlandoburli.livraria.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import br.com.orlandoburli.livraria.exceptions.LivrariaException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(LivrariaException.class)
	public final ResponseEntity<Object> handleException(final LivrariaException ex, final WebRequest request) {

		final ResponseStatus responseStatus = ex.getClass().getAnnotation(ResponseStatus.class);

		return new ResponseEntity<>(ex, responseStatus == null ? HttpStatus.BAD_REQUEST : responseStatus.value());
	}
}