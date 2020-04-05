package br.com.orlandoburli.livraria.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import br.com.orlandoburli.livraria.dto.UsuarioDto;
import br.com.orlandoburli.livraria.enums.Status;
import br.com.orlandoburli.livraria.exceptions.validations.ValidationLivrariaException;
import br.com.orlandoburli.livraria.model.Usuario;
import br.com.orlandoburli.livraria.repository.UsuarioRepository;
import br.com.orlandoburli.livraria.utils.ValidatorUtils;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository repository;

	@Autowired
	private ConversionService conversionService;

	@Autowired
	private ValidatorUtils validatorUtils;

	/**
	 * Cria um usuário.
	 * 
	 * @param usuario Dados do usuário
	 * @return Usuário criado com id retornado.
	 * @throws ValidationLivrariaException Exceção disparada se ocorrerem erros de
	 *                                     validação dos dados.
	 */
	public UsuarioDto create(UsuarioDto usuario) throws ValidationLivrariaException {
		usuario.setStatus(Status.ATIVO);

		Usuario entity = conversionService.convert(usuario, Usuario.class);

		validatorUtils.validate(entity);

		Usuario created = repository.save(entity);

		return this.conversionService.convert(created, UsuarioDto.class);
	}
}
