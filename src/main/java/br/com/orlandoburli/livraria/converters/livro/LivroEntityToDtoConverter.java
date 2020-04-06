package br.com.orlandoburli.livraria.converters.livro;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import br.com.orlandoburli.livraria.dto.LivroDto;
import br.com.orlandoburli.livraria.model.Livro;

@Component
public class LivroEntityToDtoConverter implements Converter<Livro, LivroDto>{

	@Override
	public LivroDto convert(Livro source) {
		return LivroDto
				.builder()
					.id(source.getId())
					.titulo(source.getTitulo())
					.genero(source.getGenero())
					.autor(source.getAutor())
					.sinopse(source.getSinopse())
					.status(source.getStatus())
				.build();
	}
}