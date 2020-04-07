package br.com.orlandoburli.livraria.exceptions.emprestimo;

public class LivroJaEmprestadoException extends EmprestimoException {

	private static final long serialVersionUID = 1L;

	public LivroJaEmprestadoException(final String message) {
		super(message);
	}
}