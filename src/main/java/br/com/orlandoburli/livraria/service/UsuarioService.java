package br.com.orlandoburli.livraria.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import br.com.orlandoburli.livraria.dto.UsuarioDto;
import br.com.orlandoburli.livraria.enums.Status;
import br.com.orlandoburli.livraria.enums.StatusEmprestimo;
import br.com.orlandoburli.livraria.exceptions.usuario.CpfJaExistenteException;
import br.com.orlandoburli.livraria.exceptions.usuario.UsuarioNaoEncontradoException;
import br.com.orlandoburli.livraria.exceptions.usuario.UsuarioNaoInformadoException;
import br.com.orlandoburli.livraria.exceptions.usuario.UsuarioPossuiEmprestimosAbertosException;
import br.com.orlandoburli.livraria.exceptions.validations.ValidationLivrariaException;
import br.com.orlandoburli.livraria.model.Usuario;
import br.com.orlandoburli.livraria.repository.EmprestimoRepository;
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

	@Autowired
	private EmprestimoRepository emprestimoRepository;

	/**
	 * Retorna um usuário pelo Id.
	 *
	 * @param id Id do usuário encontrado.
	 * @return Usuário encontrado.
	 * @throws UsuarioNaoEncontradoException Exceção disparada caso o usuário não
	 *                                       seja encontrado com o id informado.
	 * @throws UsuarioNaoInformadoException  Exceção disparada caso o id do usuárioo
	 *                                       não seja informado
	 */
	public UsuarioDto get(final Long id) throws UsuarioNaoEncontradoException, UsuarioNaoInformadoException {
		return conversionService.convert(validaUsuarioExistente(id), UsuarioDto.class);
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
	 * @throws CpfJaExistenteException      Exceção disparada caso o Cpf já exista
	 */
	public UsuarioDto create(final UsuarioDto usuario)
			throws ValidationLivrariaException, UsuarioNaoInformadoException, CpfJaExistenteException {

		validaCorpoUsuario(usuario);

		usuario.setStatus(Status.ATIVO);

		final Usuario entity = conversionService.convert(usuario, Usuario.class);

		validatorUtils.validate(entity);

		validaCpfExistente(entity.getCpf(), 0L);

		final Usuario created = repository.save(entity);

		return conversionService.convert(created, UsuarioDto.class);
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
	 * @throws CpfJaExistenteException       Exceção disparada caso o Cpf já exista
	 *                                       em outro usuário
	 */
	public UsuarioDto update(final UsuarioDto usuario) throws ValidationLivrariaException,
			UsuarioNaoEncontradoException, UsuarioNaoInformadoException, CpfJaExistenteException {

		validaCorpoUsuario(usuario);

		validaUsuarioExistente(usuario.getId());

		final Usuario entity = conversionService.convert(usuario, Usuario.class);

		entity.setStatus(Status.ATIVO);

		validatorUtils.validate(entity);

		validaCpfExistente(entity.getCpf(), entity.getId());

		final Usuario saved = repository.save(entity);

		return conversionService.convert(saved, UsuarioDto.class);
	}

	/**
	 * Exclui um usuário
	 *
	 * @param id Id do usuário a ser excluído.
	 * @throws UsuarioNaoEncontradoException            Exceção disparada caso o
	 *                                                  usuário não seja encontrado.
	 * @throws UsuarioNaoInformadoException             Exceção disparada caso o id
	 *                                                  do usuário não seja
	 *                                                  informado.
	 * @throws UsuarioPossuiEmprestimosAbertosException Exceção disparada caso o
	 *                                                  usuário possua emprestimos
	 *                                                  em abertos
	 */
	public void destroy(final Long id) throws UsuarioNaoEncontradoException, UsuarioNaoInformadoException,
			UsuarioPossuiEmprestimosAbertosException {
		validaUsuarioPossuiEmprestimos(id);

		final Usuario entity = validaUsuarioExistente(id);

		entity.setStatus(Status.INATIVO);

		repository.save(entity);
	}

	/**
	 * Valida se o cpf já existe em outro usuario
	 *
	 * @param cpf Cpf a ser validaddo
	 * @param id  Id do usuário, caso seja alguem já existente
	 * @throws CpfJaExistenteException Exceção disparada caso exista o cpf em outro
	 *                                 usuário
	 */
	private void validaCpfExistente(final String cpf, final Long id) throws CpfJaExistenteException {
		if (repository.findByCpfAndIdNot(cpf, id).isPresent()) {
			throw new CpfJaExistenteException(messages.get("exceptions.CpfJaExistenteException", cpf));
		}
	}

	/**
	 * Valida se o usuário possui empréstimos em aberto
	 *
	 * @param id Id do usuário
	 * @throws UsuarioPossuiEmprestimosAbertosException Exceção disparada caso o
	 *                                                  usuário possua empréstimos
	 *                                                  em aberto
	 */
	private void validaUsuarioPossuiEmprestimos(final Long id) throws UsuarioPossuiEmprestimosAbertosException {
		if (!emprestimoRepository.findByUsuarioIdAndStatus(id, StatusEmprestimo.ABERTO).isEmpty()) {
			throw new UsuarioPossuiEmprestimosAbertosException(
					messages.get("exceptions.UsuarioPossuiEmprestimosAbertosException", id));
		}
	}

	/**
	 * Valida se o usuário existe e está ativo como ID informado.
	 *
	 * @param id Id do usuário
	 * @return Entidade do usuario, caso encontrado
	 * @throws UsuarioNaoEncontradoException Exceção disparada caso o usuário não
	 *                                       seja encontrado com o id informado.
	 * @throws UsuarioNaoInformadoException  Exceção disparada caso o id do usuário
	 *                                       não seja informado.
	 */
	private Usuario validaUsuarioExistente(final Long id)
			throws UsuarioNaoEncontradoException, UsuarioNaoInformadoException {
		if (id == null) {
			throw new UsuarioNaoInformadoException(messages.get(USUARIO_NAO_INFORMADO_EXCEPTION));
		}

		return repository.findByIdAndStatus(id, Status.ATIVO).orElseThrow(
				() -> new UsuarioNaoEncontradoException(messages.get(USUARIO_NAO_ENCONTRADO_EXCEPTION, id)));
	}

	/**
	 * Valida se o usuário não é nulo
	 *
	 * @param usuario Usuário a ser validado
	 * @throws UsuarioNaoInformadoException Exceção disparada caso o usuário seja
	 *                                      nulo
	 */
	private void validaCorpoUsuario(final UsuarioDto usuario) throws UsuarioNaoInformadoException {
		if (usuario == null) {
			throw new UsuarioNaoInformadoException(messages.get(USUARIO_NAO_INFORMADO_EXCEPTION));
		}
	}
}
