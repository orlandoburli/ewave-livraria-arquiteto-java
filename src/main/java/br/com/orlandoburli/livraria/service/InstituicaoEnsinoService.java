package br.com.orlandoburli.livraria.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import br.com.orlandoburli.livraria.dto.InstituicaoEnsinoDto;
import br.com.orlandoburli.livraria.enums.Status;
import br.com.orlandoburli.livraria.exceptions.instituicaoensino.InstituicaoEnsinoComUsuariosException;
import br.com.orlandoburli.livraria.exceptions.instituicaoensino.InstituicaoEnsinoNaoEncontradaException;
import br.com.orlandoburli.livraria.exceptions.instituicaoensino.InstituicaoEnsinoNaoInformadaException;
import br.com.orlandoburli.livraria.exceptions.validations.ValidationLivrariaException;
import br.com.orlandoburli.livraria.model.InstituicaoEnsino;
import br.com.orlandoburli.livraria.repository.InstituicaoEnsinoRepository;
import br.com.orlandoburli.livraria.repository.UsuarioRepository;
import br.com.orlandoburli.livraria.utils.MessagesService;
import br.com.orlandoburli.livraria.utils.ValidatorUtils;

@Service
public class InstituicaoEnsinoService {

	private static final String INSTITUICAO_ENSINO_COM_USUARIOS_EXCEPTION = "exceptions.InstituicaoEnsinoComUsuariosException";

	private static final String INSTITUICAO_ENSINO_NAO_INFORMADA_EXCEPTION = "exceptions.InstituicaoEnsinoNaoInformadaException";

	private static final String INSTITUICAO_ENSINO_NAO_ENCONTRADA_EXCEPTION = "exceptions.InstituicaoEnsinoNaoEncontradaException";

	@Autowired
	private ConversionService conversionService;

	@Autowired
	private ValidatorUtils validatorUtils;

	@Autowired
	private InstituicaoEnsinoRepository repository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private MessagesService messages;

	/**
	 * Retorna uma instituição de ensino pelo Id.
	 * 
	 * @param id Id da instituição de ensino.
	 * @return Dados da Instituição de ensino encontrada.
	 * @throws InstituicaoEnsinoNaoEncontradaException Exceção disparada caso a
	 *                                                 instituição de ensino não
	 *                                                 seja encontrada.
	 */
	public InstituicaoEnsinoDto get(Long id) throws InstituicaoEnsinoNaoEncontradaException {
		InstituicaoEnsino entity = this.repository.findByIdAndStatus(id, Status.ATIVO)
				.orElseThrow(() -> new InstituicaoEnsinoNaoEncontradaException(
						messages.get(INSTITUICAO_ENSINO_NAO_ENCONTRADA_EXCEPTION, id)));

		return this.conversionService.convert(entity, InstituicaoEnsinoDto.class);
	}

	/**
	 * Cria uma instituição de ensino.
	 * 
	 * @param instituicaoEnsino Instituição de ensino a ser criada.
	 * @return Instituição de ensino criada, com id populado.
	 * @throws ValidationLivrariaException            Exceção disparada se ocorrerem
	 *                                                erros de validação dos dados.
	 * @throws InstituicaoEnsinoNaoInformadaException Exceção disparada quando a
	 *                                                instituição de ensino for
	 *                                                totalmente nula.
	 */
	public InstituicaoEnsinoDto create(InstituicaoEnsinoDto instituicaoEnsino)
			throws ValidationLivrariaException, InstituicaoEnsinoNaoInformadaException {

		validaCorpoInstituicaoEnsino(instituicaoEnsino);

		instituicaoEnsino.setStatus(Status.ATIVO);

		InstituicaoEnsino entity = this.conversionService.convert(instituicaoEnsino, InstituicaoEnsino.class);

		validatorUtils.validate(entity);

		InstituicaoEnsino saved = repository.save(entity);

		return this.conversionService.convert(saved, InstituicaoEnsinoDto.class);
	}

	/**
	 * Atualiza uma instituição de ensino
	 * 
	 * @param instituicaoEnsino Instituição de ensino a ser atualizada.
	 * @return Dados da instituição de ensino atualizada.
	 * @throws InstituicaoEnsinoNaoEncontradaException Exceção disparada caso a
	 *                                                 instituição não seja
	 *                                                 encontrada com o id
	 *                                                 informado.
	 * @throws ValidationLivrariaException             Exceção disparada se ocorrer
	 *                                                 erro de validação dos dados
	 * @throws InstituicaoEnsinoNaoInformadaException  Exceção disparada quando a
	 *                                                 instituição de ensino for
	 *                                                 totalmente nula.
	 */
	public InstituicaoEnsinoDto update(InstituicaoEnsinoDto instituicaoEnsino)
			throws InstituicaoEnsinoNaoEncontradaException, ValidationLivrariaException,
			InstituicaoEnsinoNaoInformadaException {

		validaCorpoInstituicaoEnsino(instituicaoEnsino);

		validaInstituicaoExistente(instituicaoEnsino.getId());

		InstituicaoEnsino entity = this.conversionService.convert(instituicaoEnsino, InstituicaoEnsino.class);

		validatorUtils.validate(entity);

		InstituicaoEnsino saved = repository.save(entity);

		return this.conversionService.convert(saved, InstituicaoEnsinoDto.class);
	}

	/**
	 * Inativa uma instituição de ensino.
	 * 
	 * @param id Id da instituição
	 * @throws InstituicaoEnsinoNaoEncontradaException Exceção disparada caso a
	 *                                                 instituição não seja
	 *                                                 encontrada com o id
	 *                                                 informado.
	 * @throws InstituicaoEnsinoComUsuariosException   Exceção disparada caso a
	 *                                                 instituição possua usuários
	 *                                                 registrados, não podendo ser
	 *                                                 excluída.
	 */
	public void destroy(Long id) throws InstituicaoEnsinoNaoEncontradaException, InstituicaoEnsinoComUsuariosException {

		validaUsuariosExistentes(id);

		InstituicaoEnsino entity = this.repository.findById(id)
				.orElseThrow(() -> new InstituicaoEnsinoNaoEncontradaException(
						messages.get(INSTITUICAO_ENSINO_NAO_ENCONTRADA_EXCEPTION, id)));

		entity.setStatus(Status.INATIVO);

		this.repository.save(entity);

	}

	private void validaCorpoInstituicaoEnsino(InstituicaoEnsinoDto instituicaoEnsino)
			throws InstituicaoEnsinoNaoInformadaException {
		if (instituicaoEnsino == null) {
			throw new InstituicaoEnsinoNaoInformadaException(
					messages.get(INSTITUICAO_ENSINO_NAO_INFORMADA_EXCEPTION));
		}
	}

	/**
	 * Valida se a instituição de ensino existe com o id informado e com o status
	 * ATIVA.
	 * 
	 * @param id Id da Instituição de ensino
	 * @return Entidade da instituição de ensino
	 * @throws InstituicaoEnsinoNaoEncontradaException Exceção disparada caso a
	 *                                                 instituição não seja
	 *                                                 encontrada com o id
	 *                                                 informado.
	 */
	private InstituicaoEnsino validaInstituicaoExistente(Long id) throws InstituicaoEnsinoNaoEncontradaException {
		return this.repository.findByIdAndStatus(id, Status.ATIVO)
				.orElseThrow(() -> new InstituicaoEnsinoNaoEncontradaException(
						messages.get(INSTITUICAO_ENSINO_NAO_ENCONTRADA_EXCEPTION, id)));
	}

	/**
	 * Valida se a instituição possui usuários existentes para ser excluída
	 * 
	 * @param id Id da Instituição de ensino
	 * @throws InstituicaoEnsinoComUsuariosException Exceção disparada caso a
	 *                                               instituição possua usuários
	 *                                               registrados.
	 */
	private void validaUsuariosExistentes(Long id) throws InstituicaoEnsinoComUsuariosException {
		Long count = this.usuarioRepository.countByInstituicaoId(id);

		if (count > 0) {
			throw new InstituicaoEnsinoComUsuariosException(
					messages.get(INSTITUICAO_ENSINO_COM_USUARIOS_EXCEPTION, id));
		}
	}
}