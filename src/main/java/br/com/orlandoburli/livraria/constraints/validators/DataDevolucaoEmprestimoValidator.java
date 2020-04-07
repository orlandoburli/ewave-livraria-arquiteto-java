package br.com.orlandoburli.livraria.constraints.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.orlandoburli.livraria.constraints.annotations.DataDevolucaoEmprestimo;
import br.com.orlandoburli.livraria.enums.StatusEmprestimo;
import br.com.orlandoburli.livraria.model.Emprestimo;

public class DataDevolucaoEmprestimoValidator implements ConstraintValidator<DataDevolucaoEmprestimo, Emprestimo> {

	@Override
	public boolean isValid(final Emprestimo value, final ConstraintValidatorContext context) {
		return value.getDataDevolucao() != null && value.getStatus() == StatusEmprestimo.DEVOLVIDO
				|| value.getStatus() == StatusEmprestimo.ABERTO;
	}
}
