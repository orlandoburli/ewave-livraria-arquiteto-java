package br.com.orlandoburli.livraria.constraints.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import br.com.orlandoburli.livraria.constraints.validators.UsuarioDeveTerEmailOuTelefoneValidator;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UsuarioDeveTerEmailOuTelefoneValidator.class)
@Documented
public @interface UsuarioDeveTerEmailOuTelefone {

	String message() default "{javax.validations.usuario.emailOuTelefone.notEmpty}";
	
    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}