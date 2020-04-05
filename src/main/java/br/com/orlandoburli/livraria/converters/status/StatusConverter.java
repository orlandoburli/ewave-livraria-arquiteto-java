package br.com.orlandoburli.livraria.converters.status;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import br.com.orlandoburli.livraria.enums.Status;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, String> {

	@Override
	public String convertToDatabaseColumn(Status attribute) {
		return attribute.getValor();
	}

	@Override
	public Status convertToEntityAttribute(String dbData) {
		return Status.from(dbData);
	}
}
