package br.com.orlandoburli.livraria.converters.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import br.com.orlandoburli.livraria.dto.UsuarioDto;
import br.com.orlandoburli.livraria.model.Usuario;
import br.com.orlandoburli.livraria.repository.InstituicaoEnsinoRepository;
import br.com.orlandoburli.livraria.utils.Utils;

@Component
public class UsuarioDtoToEntityConverter implements Converter<UsuarioDto, Usuario>{

	@Autowired
	@Lazy
	private InstituicaoEnsinoRepository instituicaoEnsinoRepository;
	
	@Override
	public Usuario convert(UsuarioDto source) {
		return Usuario
				.builder()
					.id(source.getId())
					.nome(source.getNome())
					.endereco(source.getEndereco())
					.cpf(Utils.numbersOnly(source.getCpf()))
					.telefone(Utils.numbersOnly(source.getTelefone()))
					.email(source.getEmail())
					.instituicao(source.getInstituicao() != null ? instituicaoEnsinoRepository.getOne(source.getInstituicao().getId()) : null)
					.status(source.getStatus())
				.build();
	}
}