package br.com.orlandoburli.livraria.constraints.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import br.com.orlandoburli.livraria.constraints.annotations.UsuarioDeveTerEmailOuTelefone;
import br.com.orlandoburli.livraria.model.Usuario;

public class UsuarioDeveTerEmailOuTelefoneValidator
		implements ConstraintValidator<UsuarioDeveTerEmailOuTelefone, Usuario> {

	@Override
	public boolean isValid(Usuario value, ConstraintValidatorContext context) {
		return !(StringUtils.isEmpty(value.getEmail()) && StringUtils.isEmpty(value.getTelefone()));
	}
}