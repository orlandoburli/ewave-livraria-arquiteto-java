package br.com.orlandoburli.livraria.converters.statusreserva;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import br.com.orlandoburli.livraria.enums.StatusReserva;

@Converter(autoApply = true)
public class StatusReservaConverter implements AttributeConverter<StatusReserva, String> {

	@Override
	public String convertToDatabaseColumn(final StatusReserva attribute) {
		return attribute.getValor();
	}

	@Override
	public StatusReserva convertToEntityAttribute(final String dbData) {
		return StatusReserva.from(dbData);
	}
}
