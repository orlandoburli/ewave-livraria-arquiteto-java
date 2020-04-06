package br.com.orlandoburli.livraria.exceptions.emprestimo;

public class EmprestimoNaoInformadoException extends EmprestimoException {

	private static final long serialVersionUID = 1L;

	public EmprestimoNaoInformadoException(final String message) {
		super(message);
	}
}