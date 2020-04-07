package br.com.orlandoburli.livraria.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import br.com.orlandoburli.livraria.dto.EmprestimoDto;
import br.com.orlandoburli.livraria.dto.LivroDto;
import br.com.orlandoburli.livraria.dto.UsuarioDto;
import br.com.orlandoburli.livraria.enums.StatusEmprestimo;
import br.com.orlandoburli.livraria.exceptions.emprestimo.EmprestimoJaDevolvidoException;
import br.com.orlandoburli.livraria.exceptions.emprestimo.EmprestimoNaoEncontradoException;
import br.com.orlandoburli.livraria.exceptions.emprestimo.EmprestimoNaoInformadoException;
import br.com.orlandoburli.livraria.exceptions.emprestimo.LivroJaEmprestadoException;
import br.com.orlandoburli.livraria.exceptions.emprestimo.MaximoPedidosUsuarioException;
import br.com.orlandoburli.livraria.exceptions.emprestimo.UsuarioBloqueadoPorAtrasoException;
import br.com.orlandoburli.livraria.exceptions.livro.LivroNaoEncontradoException;
import br.com.orlandoburli.livraria.exceptions.livro.LivroNaoInformadoException;
import br.com.orlandoburli.livraria.exceptions.usuario.UsuarioNaoEncontradoException;
import br.com.orlandoburli.livraria.exceptions.usuario.UsuarioNaoInformadoException;
import br.com.orlandoburli.livraria.exceptions.validations.ValidationLivrariaException;
import br.com.orlandoburli.livraria.model.Emprestimo;
import br.com.orlandoburli.livraria.model.Restricao;
import br.com.orlandoburli.livraria.repository.EmprestimoRepository;
import br.com.orlandoburli.livraria.repository.RestricaoRepository;
import br.com.orlandoburli.livraria.utils.ClockUtils;
import br.com.orlandoburli.livraria.utils.MessagesService;
import br.com.orlandoburli.livraria.utils.ValidatorUtils;

@Service
public class EmprestimoService {

	private static final int DIAS_RESTRICAO = 30;

	private static final int MAXIMO_LIVROS_POR_USUARIO = 2;

	private static final String MAXIMO_PEDIDOS_USUARIO_EXCEPTION = "exceptions.MaximoPedidosUsuarioException";

	private static final String LIVRO_JA_EMPRESTADO_EXCEPTION = "exceptions.LivroJaEmprestadoException";

	private static final String EMPRESTIMO_NAO_ENCONTRADO_EXCEPTION = "exceptions.EmprestimoNaoEncontradoException";

	private static final String EMPRESTIMO_JA_DEVOLVIDO_EXCEPTION = "exceptions.EmprestimoJaDevolvidoException";

	private static final int PRAZO_DEVOLUCAO = 30;

	@Autowired
	private EmprestimoRepository repository;

	@Autowired
	private RestricaoRepository restricaoRepository;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private LivroService livroService;

	@Autowired
	private ConversionService conversionService;

	@Autowired
	private MessagesService messages;

	@Autowired
	private ValidatorUtils validator;

	@Autowired
	private ClockUtils clock;

	@Autowired
	private NotificacaoService notificacaoService;

	/**
	 * Retorna um emprestimo pelo seu id
	 *
	 * @param id Id do empréstimo
	 * @return Emprestimo localizado
	 * @throws EmprestimoNaoEncontradoException Exceção disparada caso o empréstimo
	 *                                          não tenha sido localizado.
	 * @throws EmprestimoNaoInformadoException  Exceção disparada caso o id
	 *                                          informado seja nulo
	 */
	public EmprestimoDto get(final Long id) throws EmprestimoNaoEncontradoException, EmprestimoNaoInformadoException {

		if (id == null) {
			throw new EmprestimoNaoInformadoException(messages.get("exceptions.EmprestimoNaoInformadoException"));
		}

		final Emprestimo entity = repository.findById(id).orElseThrow(
				() -> new EmprestimoNaoEncontradoException(messages.get(EMPRESTIMO_NAO_ENCONTRADO_EXCEPTION, id)));

		return conversionService.convert(entity, EmprestimoDto.class);
	}

	public EmprestimoDto realizarEmprestimo(final Long usuarioId, final Long livroId)
			throws UsuarioNaoEncontradoException, LivroNaoEncontradoException, UsuarioNaoInformadoException,
			LivroNaoInformadoException, ValidationLivrariaException, LivroJaEmprestadoException,
			MaximoPedidosUsuarioException, UsuarioBloqueadoPorAtrasoException {

		final UsuarioDto usuario = usuarioService.get(usuarioId);

		final LivroDto livro = livroService.get(livroId);

		validaImpedimentosUsuario(usuario);

		validaImpedimentosLivro(livro);

		// @formatter:off
		final EmprestimoDto emprestimo = EmprestimoDto
				.builder()
					.livro(livro)
					.usuario(usuario)
					.dataEmprestimo(clock.hoje())
					.status(StatusEmprestimo.ABERTO)
				.build();
		// @formatter:on

		final Emprestimo entity = conversionService.convert(emprestimo, Emprestimo.class);

		validator.validate(entity);

		final EmprestimoDto emprestimoDto = conversionService.convert(repository.save(entity), EmprestimoDto.class);

		emprestimoDto.setDataPrevistaDevolucao(calculaDataDevolucao(emprestimo.getDataEmprestimo()));

		return emprestimoDto;
	}

	/**
	 * Devolve um livro
	 *
	 * @param id Id do empréstimo
	 * @throws EmprestimoNaoEncontradoException Exceção disparada caso o empréstimo
	 *                                          não seja localizado
	 * @throws EmprestimoJaDevolvidoException   Exceção disparada caso o empréstimo
	 *                                          já tenha sido devolvido
	 * @throws EmprestimoNaoInformadoException  Exceção disparada caso o id
	 *                                          informado seja nulo
	 * @throws ValidationLivrariaException      Exceção disparada caso haja algum
	 *                                          erro nos dados do emprestimo
	 */
	public void devolverLivro(final Long id) throws EmprestimoNaoEncontradoException, EmprestimoJaDevolvidoException,
			EmprestimoNaoInformadoException, ValidationLivrariaException {
		final EmprestimoDto emprestimo = get(id);

		validaLivroPodeSerDevolvido(emprestimo);

		emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);
		emprestimo.setDataDevolucao(clock.hoje());

		final Emprestimo entity = conversionService.convert(emprestimo, Emprestimo.class);

		validator.validate(entity);

		repository.save(entity);

		if (!isEmprestimoNoPrazo(emprestimo.getDataEmprestimo())) {
			registraInadimplenciaUsuario(emprestimo);
		}
	}

	/**
	 * Verifica se existe algum impedimento para o emprestimo deste livro
	 *
	 * @param livro Livro a ser verificado
	 * @throws LivroJaEmprestadoException Exceção disparada caso o livro já esteja
	 *                                    emprestado para alguém
	 */
	public void validaImpedimentosLivro(final LivroDto livro) throws LivroJaEmprestadoException {
		if (repository.findByLivroIdAndStatus(livro.getId(), StatusEmprestimo.ABERTO).isPresent()) {
			throw new LivroJaEmprestadoException(messages.get(LIVRO_JA_EMPRESTADO_EXCEPTION, livro.getId()));
		}
	}

	/**
	 * Verifica se existe algum impedimento para o usuário emprestar livros
	 *
	 * @param usuario Usuário a ser verificado
	 * @throws MaximoPedidosUsuarioException      Exceção disparada caso o usuário
	 *                                            já tenha emprestado o máximo de
	 *                                            livros permitidos
	 * @throws UsuarioBloqueadoPorAtrasoException Exceção disparada caso o usuário
	 *                                            esteja com restrição de empréstimo
	 *                                            por atraso
	 */
	public void validaImpedimentosUsuario(final UsuarioDto usuario)
			throws MaximoPedidosUsuarioException, UsuarioBloqueadoPorAtrasoException {
		final List<Emprestimo> livrosEmprestados = repository.findByUsuarioIdAndStatus(usuario.getId(),
				StatusEmprestimo.ABERTO);

		if (livrosEmprestados.size() >= MAXIMO_LIVROS_POR_USUARIO) {
			throw new MaximoPedidosUsuarioException(messages.get(MAXIMO_PEDIDOS_USUARIO_EXCEPTION, usuario.getId()));
		}

		final long livrosEmprestadosAtrasados = livrosEmprestados.stream()
				.filter(l -> !isEmprestimoNoPrazo(l.getDataEmprestimo())).count();

		final Long totalRestricoesHoje = restricaoRepository
				.countByEmprestimoUsuarioIdAndRestritoAteGreaterThanEqual(usuario.getId(), clock.hoje());

		if (totalRestricoesHoje > 0 || livrosEmprestadosAtrasados > 0) {
			throw new UsuarioBloqueadoPorAtrasoException(
					messages.get("exceptions.UsuarioBloqueadoPorAtrasoException", usuario.getId()));
		}
	}

	/**
	 * Cria um registro de inadimplência para o usuário, bloqueando-o por 30 dias.
	 *
	 * @param usuario usuário a ser bloqueado.
	 */
	private void registraInadimplenciaUsuario(final EmprestimoDto emprestimo) {
		// @formatter:off
			final Restricao restricao = Restricao
				.builder()
					.emprestimo(repository.findById(emprestimo.getId()).orElse(null))
					.restritoAte(calculaDataRestricao(clock.hoje()))
				.build();
		// @formatter:on
		restricaoRepository.save(restricao);

		notificacaoService.notificarEntregaComAtraso(emprestimo);
	}

	/**
	 * Identifica se o empréstimo está no prazo
	 *
	 * @param emprestimo Empréstimo a ser verificado
	 * @return <b>true</b> se foi devolvido no prazo, caso contrário <b>false</b>
	 */
	private boolean isEmprestimoNoPrazo(final LocalDate dataEmprestimo) {
		return clock.hoje().isBefore(calculaDataDevolucao(dataEmprestimo));
	}

	/**
	 * Verifica se o livro pode ser devolvido
	 *
	 * @param emprestimo Empréstimo a ser validado
	 * @throws EmprestimoJaDevolvidoException Exceção disparada caso o empréstimo já
	 *                                        tenha sido devolvido
	 */
	private void validaLivroPodeSerDevolvido(final EmprestimoDto emprestimo) throws EmprestimoJaDevolvidoException {
		if (emprestimo.getStatus() == StatusEmprestimo.DEVOLVIDO) {
			throw new EmprestimoJaDevolvidoException(
					messages.get(EMPRESTIMO_JA_DEVOLVIDO_EXCEPTION, emprestimo.getId()));
		}
	}

	private LocalDate calculaDataRestricao(final LocalDate dataDevolucao) {
		return dataDevolucao.plusDays(DIAS_RESTRICAO);
	}

	private LocalDate calculaDataDevolucao(final LocalDate dataEmprestimo) {
		return dataEmprestimo.plusDays(PRAZO_DEVOLUCAO);
	}
}