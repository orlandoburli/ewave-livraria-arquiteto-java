package br.com.orlandoburli.livraria.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import br.com.orlandoburli.livraria.dto.InstituicaoEnsinoDto;
import br.com.orlandoburli.livraria.enums.Status;
import br.com.orlandoburli.livraria.exceptions.LivrariaException;
import br.com.orlandoburli.livraria.model.InstituicaoEnsino;
import br.com.orlandoburli.livraria.repository.InstituicaoEnsinoRepository;
import br.com.orlandoburli.livraria.utils.ValidatorUtils;

@Service
public class InstituicaoEnsinoService {

	@Autowired
	private ConversionService conversionService;

	@Autowired
	private ValidatorUtils validatorUtils;

	@Autowired
	private InstituicaoEnsinoRepository repository;

	public InstituicaoEnsinoDto create(InstituicaoEnsinoDto instituicaoEnsino) throws LivrariaException {

		if (instituicaoEnsino.getStatus() == null)
			instituicaoEnsino.setStatus(Status.ATIVO);

		InstituicaoEnsino entity = this.conversionService.convert(instituicaoEnsino, InstituicaoEnsino.class);

		validatorUtils.validate(entity);

		InstituicaoEnsino saved = repository.save(entity);

		return this.conversionService.convert(saved, InstituicaoEnsinoDto.class);
	}
}
