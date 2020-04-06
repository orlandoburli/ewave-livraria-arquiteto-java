package br.com.orlandoburli.livraria.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import br.com.orlandoburli.livraria.dto.LivroDto;
import br.com.orlandoburli.livraria.enums.Status;
import br.com.orlandoburli.livraria.exceptions.livro.CapaNaoEncontradaException;
import br.com.orlandoburli.livraria.exceptions.livro.CapaNaoInformadaException;
import br.com.orlandoburli.livraria.exceptions.livro.LivroNaoEncontradoException;
import br.com.orlandoburli.livraria.exceptions.livro.LivroNaoInformadoException;
import br.com.orlandoburli.livraria.exceptions.validations.ValidationLivrariaException;
import br.com.orlandoburli.livraria.model.Capa;
import br.com.orlandoburli.livraria.model.CapaId;
import br.com.orlandoburli.livraria.model.Livro;
import br.com.orlandoburli.livraria.repository.CapaRepository;
import br.com.orlandoburli.livraria.repository.LivroRepository;
import br.com.orlandoburli.livraria.utils.MessagesService;
import br.com.orlandoburli.livraria.utils.ValidatorUtils;

@Service
public class LivroService {

	private static final String LIVRO_NAO_ENCONTRADO_EXCEPTION = "exceptions.LivroNaoEncontradoException";

	private static final String LIVRO_NAO_INFORMADO_EXCEPTION = "exceptions.LivroNaoInformadoException";

	@Autowired
	private LivroRepository repository;

	@Autowired
	private CapaRepository capaRepository;

	@Autowired
	private ConversionService conversionService;

	@Autowired
	private ValidatorUtils validatorUtils;

	@Autowired
	private MessagesService messages;

	/**
	 * Busca um livro pelo seu Id
	 * 
	 * @param id Id do livro a ser buscado
	 * @return Livro encontrato
	 * @throws LivroNaoEncontradoException Exceção disparada caso o livro não exista
	 */
	public LivroDto get(Long id) throws LivroNaoEncontradoException {
		return this.conversionService.convert(validaLivroExistente(id), LivroDto.class);
	}

	/**
	 * Cria um livro
	 * 
	 * @param livro Livro a ser criado
	 * @return Livro criado e populado com id
	 * @throws ValidationLivrariaException Exceção disparada se ocorrerem erros de
	 *                                     validação dos dados.
	 * @throws LivroNaoInformadoException  Exceção disparada caso o livro esteja
	 *                                     nulo
	 */
	public LivroDto create(LivroDto livro) throws ValidationLivrariaException, LivroNaoInformadoException {

		validaCorpoLivro(livro);

		livro.setStatus(Status.ATIVO);

		Livro entity = conversionService.convert(livro, Livro.class);

		validatorUtils.validate(entity);

		Livro created = repository.save(entity);

		return this.conversionService.convert(created, LivroDto.class);
	}

	/**
	 * Atualiza um livro.
	 * 
	 * @param livro Livro a ser atualizado
	 * @return Livro com os dados atualizados.
	 * @throws ValidationLivrariaException Exceção disparada se ocorrerem erros de
	 *                                     validação dos dados.
	 * @throws LivroNaoInformadoException  Exceção disparada caso o livro esteja
	 *                                     nulo
	 * @throws LivroNaoEncontradoException Exceção disparada caso o livro não exista
	 *                                     com o id informado.
	 */
	public LivroDto update(LivroDto livro)
			throws ValidationLivrariaException, LivroNaoInformadoException, LivroNaoEncontradoException {

		validaCorpoLivro(livro);

		validaLivroExistente(livro.getId());

		Livro entity = conversionService.convert(livro, Livro.class);

		entity.setStatus(Status.ATIVO);

		validatorUtils.validate(entity);

		Livro updated = repository.save(entity);

		return this.conversionService.convert(updated, LivroDto.class);
	}

	/**
	 * Exclui um livro
	 * 
	 * @param id Id do livro a ser excluído.
	 * @throws LivroNaoEncontradoException Exceção disparada caso o livro não seja
	 *                                     encontrado.
	 */
	public void destroy(Long id) throws LivroNaoEncontradoException {

		Livro entity = this.validaLivroExistente(id);

		entity.setStatus(Status.INATIVO);

		this.repository.save(entity);
	}

	/**
	 * Retorna a imagem da capa de um livro
	 * 
	 * @param id Id do livro
	 * @return Bytes da capa do livro, caso exista
	 * @throws CapaNaoEncontradaException  Exceção disparada caso não exista a capa
	 *                                     do livro.
	 * @throws LivroNaoEncontradoException Exceção disparada caso não exista o livro
	 *                                     com o id informado.
	 */
	public byte[] getCapa(Long id) throws CapaNaoEncontradaException, LivroNaoEncontradoException {
		Livro livro = this.validaLivroExistente(id);

		return capaRepository.findById(CapaId.builder().livro(livro).build())
				.orElseThrow(
						() -> new CapaNaoEncontradaException(messages.get("exceptions.CapaNaoEncontradaException", id)))
				.getImagem();
	}

	/**
	 * Salva a imagem da capa de um livro.
	 * 
	 * @param id     Id do livro
	 * @param imagem Bytes da imagem da capa do livro
	 * @throws LivroNaoEncontradoException Exceção disparada caso não exista o livro
	 *                                     informado
	 * @throws CapaNaoInformadaException   Exceção disparada caso a imagem informada
	 *                                     seja inválida
	 */
	public void saveCapa(Long id, byte[] imagem) throws LivroNaoEncontradoException, CapaNaoInformadaException {

		validaBytesImage(imagem);

		Livro livro = this.validaLivroExistente(id);

		Capa capa = capaRepository.findById(CapaId.builder().livro(livro).build())
				.orElse(Capa.builder().id(CapaId.builder().livro(livro).build()).build());

		capa.setImagem(imagem);

		capaRepository.save(capa);
	}

	/**
	 * Apaga a imagem da capa de um livro.
	 * 
	 * @param id Id do livro
	 * @throws CapaNaoEncontradaException  Exceção disparada caso não exista a capa
	 *                                     do livro.
	 * @throws LivroNaoEncontradoException Exceção disparada caso não exista o livro
	 *                                     com o id informado.
	 */
	public void destroyCapa(Long id) throws LivroNaoEncontradoException, CapaNaoEncontradaException {
		Livro livro = this.validaLivroExistente(id);

		Capa capa = capaRepository.findById(CapaId.builder().livro(livro).build()).orElseThrow(
				() -> new CapaNaoEncontradaException(messages.get("exceptions.CapaNaoEncontradaException", id)));

		capaRepository.delete(capa);
	}

	/**
	 * Valida se os bytes são de uma imagem válida
	 * @param imagem Bytes da imagem a ser validada
	 * @throws CapaNaoInformadaException Exceção disparada caso a imagem informada
	 *                                   seja inválida
	 * 
	 */
	private void validaBytesImage(byte[] imagem) throws CapaNaoInformadaException {
		if (imagem == null || imagem.length <= 10) {
			throw new CapaNaoInformadaException(messages.get("exceptions.CapaNaoInformadaException"));
		}
	}

	/**
	 * Valida se o livro existe, e caso exista, retorna o mesmo.
	 * 
	 * @param id Id do livro a ser buscado
	 * @return Livro encontrado
	 * @throws LivroNaoEncontradoException Exceção disparada caso o livro não seja
	 *                                     encontrado.
	 */
	private Livro validaLivroExistente(Long id) throws LivroNaoEncontradoException {
		return this.repository.findByIdAndStatus(id, Status.ATIVO)
				.orElseThrow(() -> new LivroNaoEncontradoException(messages.get(LIVRO_NAO_ENCONTRADO_EXCEPTION, id)));
	}

	/**
	 * Valida se o livro foi informado
	 * 
	 * @param livro Livro a ser validado
	 * @throws LivroNaoInformadoException Exceção disparada caso o livro esteja nulo
	 */
	private void validaCorpoLivro(LivroDto livro) throws LivroNaoInformadoException {
		if (livro == null) {
			throw new LivroNaoInformadoException(messages.get(LIVRO_NAO_INFORMADO_EXCEPTION));
		}
	}
}