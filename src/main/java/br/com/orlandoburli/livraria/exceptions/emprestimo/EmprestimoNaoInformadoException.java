package br.com.orlandoburli.livraria.exceptions.emprestimo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class EmprestimoNaoInformadoException extends EmprestimoException {

	private static final long serialVersionUID = 1L;

	public EmprestimoNaoInformadoException(final String message) {
		super(message);
	}
}