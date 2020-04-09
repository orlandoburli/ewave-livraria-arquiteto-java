package br.com.orlandoburli.livraria.converters.emprestimo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import br.com.orlandoburli.livraria.dto.EmprestimoDto;
import br.com.orlandoburli.livraria.model.Emprestimo;
import br.com.orlandoburli.livraria.repository.LivroRepository;
import br.com.orlandoburli.livraria.repository.UsuarioRepository;

@Component
public class EmprestimoDtoToEntityConverter implements Converter<EmprestimoDto, Emprestimo> {

	@Autowired
	@Lazy
	private UsuarioRepository usuarioRepository;

	@Autowired
	@Lazy
	private LivroRepository livroRepository;

	@Override
	public Emprestimo convert(final EmprestimoDto source) {

		// @formatter:off
		return Emprestimo
				.builder()
					.id(source.getId())
					.dataEmprestimo(source.getDataEmprestimo())
					.dataDevolucao(source.getDataDevolucao())
					.status(source.getStatus())
					.livro(livroRepository.findById(source.getLivro().getId()).orElse(null))
					.usuario(usuarioRepository.findById(source.getUsuario().getId()).orElse(null))
				.build();
	}
}