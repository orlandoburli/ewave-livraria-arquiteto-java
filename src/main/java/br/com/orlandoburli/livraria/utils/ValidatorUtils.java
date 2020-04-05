package br.com.orlandoburli.livraria.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.orlandoburli.livraria.exceptions.validations.ValidationLivrariaException;

@Component
public class ValidatorUtils {
	
	@Autowired
	private ValidatorFactory factory;

	public <T> void validate(final T vo) throws ValidationLivrariaException {
		final Validator validator = factory.getValidator();

		final Set<ConstraintViolation<T>> constraintViolations = validator.validate(vo);

		if (!constraintViolations.isEmpty()) {
			final Map<String, Set<String>> errors = new HashMap<>();

			final Iterator<ConstraintViolation<T>> iterator = constraintViolations.iterator();

			while (iterator.hasNext()) {
				final ConstraintViolation<T> cv = iterator.next();

				Set<String> errosField = errors.get(cv.getPropertyPath().toString());
				if (errosField == null) {
					errosField = new HashSet<>();
				}
				errosField.add(cv.getMessage());
				errors.put(cv.getPropertyPath().toString(), errosField);
			}

			throw new ValidationLivrariaException("Erro ao salvar dados", errors);
		}
	}
}
