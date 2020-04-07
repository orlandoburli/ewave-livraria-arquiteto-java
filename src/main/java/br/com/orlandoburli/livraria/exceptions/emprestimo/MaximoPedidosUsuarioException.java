package br.com.orlandoburli.livraria.exceptions.emprestimo;

public class MaximoPedidosUsuarioException extends EmprestimoException {

	private static final long serialVersionUID = 1L;

	public MaximoPedidosUsuarioException(final String message) {
		super(message);
	}
}