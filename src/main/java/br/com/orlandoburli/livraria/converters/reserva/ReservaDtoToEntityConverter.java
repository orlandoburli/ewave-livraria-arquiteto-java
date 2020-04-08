package br.com.orlandoburli.livraria.converters.reserva;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import br.com.orlandoburli.livraria.dto.ReservaDto;
import br.com.orlandoburli.livraria.model.Reserva;
import br.com.orlandoburli.livraria.repository.LivroRepository;
import br.com.orlandoburli.livraria.repository.UsuarioRepository;

@Component
public class ReservaDtoToEntityConverter implements Converter<ReservaDto, Reserva> {

	@Autowired
	@Lazy
	private UsuarioRepository usuarioRepository;

	@Autowired
	@Lazy
	private LivroRepository livroRepository;

	@Override
	public Reserva convert(final ReservaDto source) {
		// @formatter:off
		return Reserva
				.builder()
					.id(source.getId())
					.usuario(usuarioRepository.findById(source.getUsuario().getId()).orElse(null))
					.livro(livroRepository.findById(source.getLivro().getId()).orElse(null))
					.dataReserva(source.getDataReserva())
				.build();
	}
}