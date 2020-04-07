package br.com.orlandoburli.livraria.constraints.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import br.com.orlandoburli.livraria.constraints.validators.DataDevolucaoEmprestimoValidator;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DataDevolucaoEmprestimoValidator.class)
@Documented
public @interface DataDevolucaoEmprestimo {

	String message() default "{javax.validations.emprestimo.dataDevolucao.notNull}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}