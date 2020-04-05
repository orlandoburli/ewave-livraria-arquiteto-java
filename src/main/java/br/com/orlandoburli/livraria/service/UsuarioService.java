package br.com.orlandoburli.livraria.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import br.com.orlandoburli.livraria.dto.UsuarioDto;
import br.com.orlandoburli.livraria.enums.Status;
import br.com.orlandoburli.livraria.exceptions.usuario.UsuarioNaoEncontradoException;
import br.com.orlandoburli.livraria.exceptions.usuario.UsuarioNaoInformadoException;
import br.com.orlandoburli.livraria.exceptions.validations.ValidationLivrariaException;
import br.com.orlandoburli.livraria.model.Usuario;
import br.com.orlandoburli.livraria.repository.UsuarioRepository;
import br.com.orlandoburli.livraria.utils.MessagesService;
import br.com.orlandoburli.livraria.utils.ValidatorUtils;

@Service
public class UsuarioService {

	private static final String USUARIO_NAO_ENCONTRADO_EXCEPTION = "exceptions.UsuarioNaoEncontradoException";

	private static final String USUARIO_NAO_INFORMADO_EXCEPTION = "exceptions.UsuarioNaoInformadoException";

	@Autowired
	private UsuarioRepository repository;

	@Autowired
	private ConversionService conversionService;

	@Autowired
	private ValidatorUtils validatorUtils;

	@Autowired
	private MessagesService messages;

	/**
	 * Retorna um usuário pelo Id.
	 * 
	 * @param id Id do usuário encontrado.
	 * @return Usuário encontrado.
	 * @throws UsuarioNaoEncontradoException Exceção disparada caso o usuário não
	 *                                       seja encontrado com o id informado.
	 */
	public UsuarioDto get(Long id) throws UsuarioNaoEncontradoException {
		Usuario entity = this.repository.findByIdAndStatus(id, Status.ATIVO).orElseThrow(
				() -> new UsuarioNaoEncontradoException(messages.get(USUARIO_NAO_ENCONTRADO_EXCEPTION, id)));

		return this.conversionService.convert(entity, UsuarioDto.class);
	}

	/**
	 * Cria um usuário.
	 * 
	 * @param usuario Usuário a ser criado.
	 * @return Usuário criado com id retornado.
	 * @throws ValidationLivrariaException  Exceção disparada se ocorrerem erros de
	 *                                      validação dos dados.
	 * @throws UsuarioNaoInformadoException Exceção disparada caso o usuário seja
	 *                                      nulo
	 */
	public UsuarioDto create(UsuarioDto usuario) throws ValidationLivrariaException, UsuarioNaoInformadoException {

		validaCorpoUsuario(usuario);

		usuario.setStatus(Status.ATIVO);

		Usuario entity = conversionService.convert(usuario, Usuario.class);

		validatorUtils.validate(entity);

		Usuario created = repository.save(entity);

		return this.conversionService.convert(created, UsuarioDto.class);
	}

	/**
	 * Atualiza um usuário.
	 * 
	 * @param usuario Usuário a ser atualizado.
	 * @return Usuário atualizado.
	 * @throws ValidationLivrariaException   Exceção disparada se ocorrerem erros de
	 *                                       validação dos dados.
	 * @throws UsuarioNaoEncontradoException Exceção disparada caso o usuário não
	 *                                       exista com o id informado.
	 * @throws UsuarioNaoInformadoException  Exceção disparada caso o usuário
	 *                                       enviado seja nulo
	 */
	public UsuarioDto update(UsuarioDto usuario)
			throws ValidationLivrariaException, UsuarioNaoEncontradoException, UsuarioNaoInformadoException {

		validaCorpoUsuario(usuario);

		validaUsuarioExistente(usuario.getId());

		Usuario entity = this.conversionService.convert(usuario, Usuario.class);

		validatorUtils.validate(entity);

		Usuario saved = repository.save(entity);

		return this.conversionService.convert(saved, UsuarioDto.class);
	}

	/**
	 * Exclui um usuário
	 * 
	 * @param id Id do usuário a ser excluído.
	 * @throws UsuarioNaoEncontradoException Exceção disparada caso o usuário não
	 *                                       seja encontrado.
	 */
	public void destroy(Long id) throws UsuarioNaoEncontradoException {
		validaUsuarioPossuiEmprestimos(id);

		Usuario entity = this.repository.findById(id).orElseThrow(
				() -> new UsuarioNaoEncontradoException(messages.get(USUARIO_NAO_ENCONTRADO_EXCEPTION, id)));

		entity.setStatus(Status.INATIVO);

		this.repository.save(entity);
	}

	private void validaUsuarioPossuiEmprestimos(Long id) {
		// TODO Auto-generated method stub

	}

	/**
	 * Valida se o usuário existe e está ativo como ID informado.
	 * 
	 * @param id Id do usuário
	 * @return Entidade do usuario, caso encontrado
	 * @throws UsuarioNaoEncontradoException Exceção disparada caso o usuário não
	 *                                       seja encontrado com o id informado.
	 */
	private Usuario validaUsuarioExistente(Long id) throws UsuarioNaoEncontradoException {
		return this.repository.findByIdAndStatus(id, Status.ATIVO).orElseThrow(
				() -> new UsuarioNaoEncontradoException(messages.get(USUARIO_NAO_ENCONTRADO_EXCEPTION, id)));
	}

	/**
	 * Valida se o usuário não é nulo
	 * 
	 * @param usuario Usuário a ser validado
	 * @throws UsuarioNaoInformadoException Exceção disparada caso o usuário seja
	 *                                      nulo
	 */
	private void validaCorpoUsuario(UsuarioDto usuario) throws UsuarioNaoInformadoException {

		if (usuario == null) {
			throw new UsuarioNaoInformadoException(messages.get(USUARIO_NAO_INFORMADO_EXCEPTION));
		}

	}
}
