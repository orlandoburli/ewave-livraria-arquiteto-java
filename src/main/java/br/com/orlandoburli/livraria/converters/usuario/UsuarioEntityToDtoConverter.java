package br.com.orlandoburli.livraria.converters.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import br.com.orlandoburli.livraria.dto.InstituicaoEnsinoDto;
import br.com.orlandoburli.livraria.dto.UsuarioDto;
import br.com.orlandoburli.livraria.model.Usuario;
import br.com.orlandoburli.livraria.utils.Utils;

@Component
public class UsuarioEntityToDtoConverter implements Converter<Usuario, UsuarioDto>{

	@Autowired
	@Lazy
	private ConversionService conversionService;
	
	
	@Override
	public UsuarioDto convert(Usuario source) {
		if (source == null) {
			return null;
		}
		
		return UsuarioDto
				.builder()
					.id(source.getId())
					.nome(source.getNome())
					.endereco(source.getEndereco())
					.cpf(Utils.numbersOnly(source.getCpf()))
					.telefone(Utils.numbersOnly(source.getTelefone()))
					.email(source.getEmail())
					.instituicao(this.conversionService
							.convert(source.getInstituicao(), InstituicaoEnsinoDto.class))
					.status(source.getStatus())
				.build();
	}
}
