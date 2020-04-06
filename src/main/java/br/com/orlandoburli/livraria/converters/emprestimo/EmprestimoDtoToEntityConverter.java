package br.com.orlandoburli.livraria.converters.emprestimo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import br.com.orlandoburli.livraria.dto.EmprestimoDto;
import br.com.orlandoburli.livraria.enums.Status;
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
		return Emprestimo
				.builder()
					.id(source.getId())
					.dataEmprestimo(source.getDataEmprestimo())
					.dataDevolucao(source.getDataDevolucao())
					.status(source.getStatus())
					.livro(source.getLivro() != null && source.getLivro().getId() != null
					? livroRepository.findById(source.getLivro().getId()).orElse(null)
							: null)
					.usuario(source.getUsuario() != null && source.getUsuario().getId() != null ?
							usuarioRepository.findByIdAndStatus(source.getUsuario().getId(), Status.ATIVO).orElse(null)
							: null)
				.build();
	}

}
