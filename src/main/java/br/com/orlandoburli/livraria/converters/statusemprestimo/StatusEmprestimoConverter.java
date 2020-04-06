package br.com.orlandoburli.livraria.converters.statusemprestimo;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import br.com.orlandoburli.livraria.enums.StatusEmprestimo;

@Converter(autoApply = true)
public class StatusEmprestimoConverter implements AttributeConverter<StatusEmprestimo, String> {

	@Override
	public String convertToDatabaseColumn(final StatusEmprestimo attribute) {
		return attribute.getValor();
	}

	@Override
	public StatusEmprestimo convertToEntityAttribute(final String dbData) {
		return StatusEmprestimo.from(dbData);
	}
}