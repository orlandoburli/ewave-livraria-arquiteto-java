package br.com.orlandoburli.livraria.converters.emprestimo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import br.com.orlandoburli.livraria.dto.EmprestimoDto;
import br.com.orlandoburli.livraria.dto.LivroDto;
import br.com.orlandoburli.livraria.dto.UsuarioDto;
import br.com.orlandoburli.livraria.model.Emprestimo;

@Component
public class EmprestimoEntityToDtoConvert implements Converter<Emprestimo, EmprestimoDto> {

	@Autowired
	private ConversionService conversionService;

	@Override
	public EmprestimoDto convert(final Emprestimo source) {
		return EmprestimoDto
				.builder()
					.id(source.getId())
					.dataEmprestimo(source.getDataEmprestimo())
					.dataDevolucao(source.getDataDevolucao())
					.status(source.getStatus())
					.livro(conversionService.convert(source.getLivro(), LivroDto.class))
					.usuario(conversionService.convert(source.getUsuario(), UsuarioDto.class))
				.build();
	}

}
