package br.com.orlandoburli.livraria.converters.instuicaoensino;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import br.com.orlandoburli.livraria.dto.InstituicaoEnsinoDto;
import br.com.orlandoburli.livraria.model.InstituicaoEnsino;

@Component
public class InstituicaoEnsinoEntityToDtoConverter implements Converter<InstituicaoEnsino, InstituicaoEnsinoDto>{

	@Override
	public InstituicaoEnsinoDto convert(InstituicaoEnsino source) {
		return InstituicaoEnsinoDto
				.builder()
					.id(source.getId())
					.nome(source.getNome())
					.telefone(source.getTelefone())
					.cnpj(source.getCnpj())
					.endereco(source.getEndereco())
					.status(source.getStatus())
				.build();
	}

}
