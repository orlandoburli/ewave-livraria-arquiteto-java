package br.com.orlandoburli.livraria.converters.instuicaoensino;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import br.com.orlandoburli.livraria.dto.InstituicaoEnsinoDto;
import br.com.orlandoburli.livraria.model.InstituicaoEnsino;
import br.com.orlandoburli.livraria.utils.Utils;

@Component
public class InstituicaoEnsinoDtoToEntityConverter implements Converter<InstituicaoEnsinoDto, InstituicaoEnsino>{

	@Override
	public InstituicaoEnsino convert(InstituicaoEnsinoDto source) {
		return InstituicaoEnsino
				.builder()
					.id(source.getId())
					.nome(source.getNome())
					.telefone(Utils.numbersOnly(source.getTelefone()))
					.cnpj(Utils.numbersOnly(source.getCnpj()))
					.endereco(source.getEndereco())
					.status(source.getStatus())
				.build();
	}
}
