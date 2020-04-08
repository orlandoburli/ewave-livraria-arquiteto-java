package br.com.orlandoburli.livraria.converters.reserva;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import br.com.orlandoburli.livraria.dto.LivroDto;
import br.com.orlandoburli.livraria.dto.ReservaDto;
import br.com.orlandoburli.livraria.dto.UsuarioDto;
import br.com.orlandoburli.livraria.model.Reserva;

@Component
public class ReservaEntityToDtoConverter implements Converter<Reserva, ReservaDto> {

	@Autowired
	@Lazy
	private ConversionService conversionService;

	@Override
	public ReservaDto convert(final Reserva source) {
		// @formatter:off
		return ReservaDto
				.builder()
					.id(source.getId())
					.usuario(conversionService.convert(source.getUsuario(), UsuarioDto.class))
					.livro(conversionService.convert(source.getLivro(), LivroDto.class))
					.dataReserva(source.getDataReserva())
				.build();
	}

}
