package br.com.orlandoburli.livraria.exceptions.emprestimo;

public class EmprestimoJaDevolvidoException extends EmprestimoException {

	private static final long serialVersionUID = 1L;

	public EmprestimoJaDevolvidoException(final String message) {
		super(message);
	}
}