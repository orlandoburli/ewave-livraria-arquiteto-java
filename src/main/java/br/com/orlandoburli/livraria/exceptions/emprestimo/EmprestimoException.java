package br.com.orlandoburli.livraria.exceptions.emprestimo;

import br.com.orlandoburli.livraria.exceptions.LivrariaException;

public class EmprestimoException extends LivrariaException {

	private static final long serialVersionUID = 1L;

	public EmprestimoException(final String message) {
		super(message);
	}
}