package br.com.orlandoburli.livraria.exceptions.emprestimo;

public class LivroJaReservadoException extends EmprestimoException {

	private static final long serialVersionUID = 1L;

	public LivroJaReservadoException(final String message) {
		super(message);
	}
}