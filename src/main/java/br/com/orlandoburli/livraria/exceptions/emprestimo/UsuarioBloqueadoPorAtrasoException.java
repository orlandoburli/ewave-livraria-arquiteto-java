package br.com.orlandoburli.livraria.exceptions.emprestimo;

public class UsuarioBloqueadoPorAtrasoException extends EmprestimoException {

	private static final long serialVersionUID = 1L;

	public UsuarioBloqueadoPorAtrasoException(final String message) {
		super(message);
	}
}